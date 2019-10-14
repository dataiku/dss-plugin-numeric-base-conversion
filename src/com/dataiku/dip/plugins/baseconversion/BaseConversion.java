package com.dataiku.dip.plugins.baseconversion;

import java.math.BigInteger;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.Processor;
import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.datalayer.SingleInputSingleOutputRowProcessor;
import com.dataiku.dip.shaker.model.StepParams;
import com.dataiku.dip.shaker.processors.Category;
import com.dataiku.dip.shaker.processors.ProcessorMeta;
import com.dataiku.dip.shaker.processors.ProcessorTag;
import com.dataiku.dip.shaker.server.ProcessorDesc;
import com.dataiku.dip.shaker.text.Labelled;
import com.dataiku.dip.util.ParamDesc;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;


public class BaseConversion extends SingleInputSingleOutputRowProcessor implements Processor {

    private static Pattern HEXADECIMAL_RE = Pattern.compile("(0[xX])?([0-9a-fA-F]+)[hH]?");
    private static Pattern BINARY_RE = Pattern.compile("([0-1]+)[bB]?");
    private static Pattern DECIMAL_RE = Pattern.compile("^([0-9]+)$");

    public static class Parameter implements StepParams {
        private static final long serialVersionUID = -1;
        public String inputColumn;
        public String outputColumn;
        public ProcessingMode processingMode;

        @Override
        public void validate() throws IllegalArgumentException {
            // Throw an exception if the processingMode is invalid.
            BaseConversion.newConverter(processingMode);
        }
    }

    public enum ProcessingMode implements Labelled {
        BINARY_TO_DECIMAL {
            public String getLabel() {
                return "Binary to Decimal";
            }
        },
        HEXA_TO_DECIMAL {
            public String getLabel() {
                return "Hexadecimal to Decimal";
            }
        },
        DECIMAL_TO_BINARY {
            public String getLabel() {
                return "Decimal to Binary";
            }
        },
        DECIMAL_TO_HEXA {
            public String getLabel() {
                return "Decimal to Hexadecimal";
            }
        },
        HEXA_TO_BINARY {
            public String getLabel() {
                return "Hexadecimal to Binary";
            }
        },
        BINARY_TO_HEXA {
            public String getLabel() {
                return "Binary to Hexadecimal";
            }
        }
    }

    public static final ProcessorMeta<BaseConversion, Parameter> META = new ProcessorMeta<BaseConversion, Parameter>() {

        @Override
        public String getName() {
            return "BaseConversion";
        }

        @Override
        public String getDocPage() {
            return "base-conversion";
        }

        @Override
        public Category getCategory() {
            return Category.TRANSFORMATION;
        }

        @Override
        public Set<ProcessorTag> getTags() {
            return Sets.newHashSet(ProcessorTag.RESHAPING, ProcessorTag.MATH);
        }

        @Override
        public String getHelp() {
            return "This processor performs base conversion." +
                    "\n \n" +
                    "# Output \n" +
                    "* <i>prefix</i>convert : Contains converted numbers";
        }

        @Override
        public Class<Parameter> stepParamClass() {
            return Parameter.class;
        }

        @Override
        public ProcessorDesc describe() {
            return ProcessorDesc.withGenericForm(this.getName(), actionVerb("Convert") + " binary hexa to from decimal")
                    .withMNEColParam("inputColumn", "Input column")
                    .withParam(ParamDesc.advancedSelect("processingMode", "Conversion", "", ProcessingMode.class).withDefaultValue(ProcessingMode.BINARY_TO_DECIMAL))
                    .withMNESParam("outputColumn", "Output column");
        }

        @Override
        public BaseConversion build(BaseConversion.Parameter parameter) throws Exception {
            return new BaseConversion(parameter);
        }
    };

    public BaseConversion(Parameter params) {
        this.params = params;
    }

    private Parameter params;
    private Converter selectedConverter;
    private Column outputColumn;
    private Column cd;

    @Override
    public void processRow(Row row) throws Exception {
        String str = row.get(cd);
        if (str == null || str.equals("")) {
            getProcessorOutput().emitRow(row);
            return;
        }

        String output = selectedConverter.convert(str);
        if (output.length() != 0) {
            row.put(outputColumn, output);
        }

        getProcessorOutput().emitRow(row);
    }

    @Override
    public void postProcess() throws Exception {
        getProcessorOutput().lastRowEmitted();
    }

    @Override
    public void init() throws Exception {
        cd = getCf().column(params.inputColumn, ProcessorRole.INPUT_COLUMN);
        outputColumn = getCf().column(params.outputColumn, ProcessorRole.OUTPUT_COLUMN);
        selectedConverter = newConverter(params.processingMode);
    }

    @VisibleForTesting
    static Converter newConverter(ProcessingMode processingMode) {
        switch (processingMode) {
        case HEXA_TO_DECIMAL:
            return new HexaToDecimal();
        case BINARY_TO_DECIMAL:
            return new BinaryToDecimal();
        case DECIMAL_TO_HEXA:
            return new DecimalToHexa();
        case DECIMAL_TO_BINARY:
            return new DecimalToBinary();
        case HEXA_TO_BINARY:
            return new HexaToBinary();
        case BINARY_TO_HEXA:
            return new BinaryToHexa();
        default:
            throw new IllegalArgumentException("Invalid processing mode: " + processingMode);
        }
    }

    @VisibleForTesting
    interface Converter {
        String convert(String toConvert);
    }

    private static class BinaryToDecimal implements Converter {
        @Override
        public String convert(String str) {
            Preconditions.checkNotNull(str, "str cannot be null");

            Matcher matcher = BINARY_RE.matcher(str);
            if (matcher.matches()) {
                BigInteger input = new BigInteger(matcher.group(1), 2);
                return input.toString(10);
            }
            return "";
        }
    }

    private static class HexaToDecimal implements Converter {
        @Override
        public String convert(String str) {
            Preconditions.checkNotNull(str, "str cannot be null");

            Matcher matcher = HEXADECIMAL_RE.matcher(str);
            if (matcher.matches()) {
                BigInteger input = new BigInteger(matcher.group(2), 16);
                return input.toString(10);
            }
            return "";
        }
    }

    private static class DecimalToBinary implements Converter {
        @Override
        public String convert(String str) {
            Preconditions.checkNotNull(str, "str cannot be null");

            Matcher matcher = DECIMAL_RE.matcher(str);
            if (matcher.matches()) {
                BigInteger input = new BigInteger(matcher.group(1), 10);
                return input.toString(2);
            }
            return "";
        }
    }

    private static class DecimalToHexa implements Converter {
        @Override
        public String convert(String str) {
            Preconditions.checkNotNull(str, "str cannot be null");

            Matcher matcher = DECIMAL_RE.matcher(str);
            if (matcher.matches()) {
                BigInteger input = new BigInteger(matcher.group(1), 10);
                return input.toString(16).toUpperCase();
            }
            return "";
        }
    }

    private static class HexaToBinary implements Converter {
        @Override
        public String convert(String str) {
            Preconditions.checkNotNull(str, "str cannot be null");

            Matcher matcher = HEXADECIMAL_RE.matcher(str);
            if (matcher.matches()) {
                BigInteger input = new BigInteger(matcher.group(2), 16);
                return input.toString(2);
            }
            return "";
        }
    }

    private static class BinaryToHexa implements Converter {
        @Override
        public String convert(String str) {
            Preconditions.checkNotNull(str, "str cannot be null");

            Matcher matcher = BINARY_RE.matcher(str);
            if (matcher.matches()) {
                BigInteger input = new BigInteger(matcher.group(1), 2);
                return input.toString(16).toUpperCase();
            }
            return "";
        }
    }
}