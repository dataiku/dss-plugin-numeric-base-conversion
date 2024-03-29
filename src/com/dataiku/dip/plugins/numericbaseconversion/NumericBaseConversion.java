package com.dataiku.dip.plugins.numericbaseconversion;

import java.math.BigInteger;
import java.util.Locale;
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
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;


public class NumericBaseConversion extends SingleInputSingleOutputRowProcessor implements Processor {

    private static Pattern HEXADECIMAL_RE = Pattern.compile("(?:0[xX])?([0-9a-fA-F]+)[hH]?");
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
            NumericBaseConversion.newConverter(processingMode);
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

    public static final ProcessorMeta<NumericBaseConversion, Parameter> META = new ProcessorMeta<NumericBaseConversion, Parameter>() {

        @Override
        public String getName() {
            return "NumericBaseConversion";
        }

        @Override
        public String getDocPage() {
            return "numeric-base-conversion";
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
                    "# Input column\n" +
                    "Contains the numbers to be converted\n \n" +
                    "# Conversion\n" +
                    "Select the conversion mode to / from:\n" +
                    "* Decimal\n" +
                    "* Binary\n" +
                    "* Hexadecimal\n \n" +
                    "# Output column\n" +
                    "Contains converted numbers";
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
                    .withColParam("outputColumn", "Output column");
        }

        @Override
        public NumericBaseConversion build(NumericBaseConversion.Parameter parameter) throws Exception {
            return new NumericBaseConversion(parameter);
        }
    };

    public NumericBaseConversion(Parameter params) {
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
        if (!StringUtils.isBlank(params.outputColumn)) {
            outputColumn = getCf().column(params.outputColumn, ProcessorRole.OUTPUT_COLUMN);
        } else {
            outputColumn = cd;
        }
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
    static abstract class Converter {

        abstract String convert(String toConvert);

        String convert(String str, int sourceBase, Pattern sourceBaseRegexp, int destinationBase) {
            if (str == null) {
                throw new NullPointerException("str cannot be null");
            }

            Matcher matcher = sourceBaseRegexp.matcher(str);
            if (!matcher.matches()) {
                return "";
            }
            BigInteger input = new BigInteger(matcher.group(1), sourceBase);
            return input.toString(destinationBase).toUpperCase(Locale.ROOT);
        }
    }

    private static class BinaryToDecimal extends Converter {
        @Override
        public String convert(String str) {
            return convert(str, 2, BINARY_RE, 10);
        }
    }

    private static class HexaToDecimal extends Converter {
        @Override
        public String convert(String str) {
            return convert(str, 16, HEXADECIMAL_RE, 10);
        }
    }

    private static class DecimalToBinary extends Converter {
        @Override
        public String convert(String str) {
            return convert(str, 10, DECIMAL_RE, 2);
        }
    }

    private static class DecimalToHexa extends Converter {
        @Override
        public String convert(String str) {
            return convert(str, 10, DECIMAL_RE, 16);
        }
    }

    private static class HexaToBinary extends Converter {
        @Override
        public String convert(String str) {
            return convert(str, 16, HEXADECIMAL_RE, 2);
        }
    }

    private static class BinaryToHexa extends Converter {
        @Override
        public String convert(String str) {
            return convert(str, 2, BINARY_RE, 16);
        }
    }

}