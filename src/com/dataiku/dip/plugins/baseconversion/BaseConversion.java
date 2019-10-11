package com.dataiku.dip.plugins.baseconversion;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.Processor;
import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.datalayer.SingleInputSingleOutputRowProcessor;

import com.dataiku.dip.shaker.model.StepParams;
import com.dataiku.dip.shaker.processors.*;
import com.dataiku.dip.shaker.server.ProcessorDesc;
import com.dataiku.dip.shaker.text.Labelled;
import com.dataiku.dip.shaker.processors.ProcessorTag;
import com.dataiku.dip.util.ParamDesc;
import com.dataiku.dip.utils.JSON;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigInteger;


public class BaseConversion extends SingleInputSingleOutputRowProcessor implements Processor {

    private static Pattern HEXADECIMAL_RE = Pattern.compile("(0x)?([0-9a-fA-F]+)h?");
    private static Pattern BINARY_RE = Pattern.compile("([0-1]+)b?");
    private static Pattern DECIMAL_RE = Pattern.compile("^([0-9]+)$");

    public static class Parameter implements StepParams {
        private static final long serialVersionUID=-1;
        public String inputColumn;
        public String outputColumn;
        public ProcessingMode processingMode;
        public String apiKey;

        @Override
        public void validate() throws IllegalArgumentException {}
    }

    public enum ProcessingMode implements Labelled {
        BINARYTODECIMAL {
            @Override
            public String getLabel() {
                return "Binary to Decimal";
            }
        },
        HEXADECIMALTODECIMAL {
            @Override
            public String getLabel() {
                return "Hexadecimal to Decimal";
            }
        },
        DECIMALTOBINARY {
            @Override
            public String getLabel() {
                return "Decimal to Binary";
            }
        },
        DECIMALTOHEXADECIMAL {
            @Override
            public String getLabel() {
                return "Decimal to Hexadecimal";
            }
        },
        HEXADECIMALTOBINARY {
            @Override
            public String getLabel() {
                return "Hexadecimal to Binary";
            }
        },
        BINARYTOHEXADECIMAL {
            @Override
            public String getLabel() {
                return "Binary to Hexadecimal";
            }
        }
    };

    static String binaryToDecimal(String toConvert) {
        Matcher matcher = BINARY_RE.matcher(toConvert);
        if (matcher.matches()) {
            BigInteger input = new BigInteger(matcher.group(1), 2);
            return input.toString(10);
        }
        return "";
    }

    static String hexadecimalToDecimal(String toConvert) {
        Matcher matcher = HEXADECIMAL_RE.matcher(toConvert);
        if (matcher.matches()) {
            BigInteger input = new BigInteger(matcher.group(2), 16);
            return input.toString(10);
        }
        return "";
    }

    static String decimalToBinary(String toConvert) {
        Matcher matcher = DECIMAL_RE.matcher(toConvert);
        if (matcher.matches()) {
            BigInteger input = new BigInteger(matcher.group(1), 10);
            return input.toString(2);
        }
        return "";
    }

    static String decimalToHexadecimal(String toConvert) {
        Matcher matcher = DECIMAL_RE.matcher(toConvert);
        if (matcher.matches()) {
            BigInteger input = new BigInteger(matcher.group(1), 10);
            return input.toString(16).toUpperCase();
        }
        return "";
    }

    static String hexadecimalToBinary(String toConvert) {
        Matcher matcher = HEXADECIMAL_RE.matcher(toConvert);
        if (matcher.matches()) {
            BigInteger input = new BigInteger(matcher.group(2), 16);
            return input.toString(2);
        }
        return "";
    }

    static String binaryToHexadecimal(String toConvert) {
        Matcher matcher = BINARY_RE.matcher(toConvert);
        if (matcher.matches()) {
            BigInteger input = new BigInteger(matcher.group(1), 2);
            return input.toString(16).toUpperCase();
        }
        return "";
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
            return "This processor performs base conversion."+
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
                    .withParam(ParamDesc.advancedSelect("processingMode","Conversion", "", ProcessingMode.class).withDefaultValue(ProcessingMode.BINARYTODECIMAL))
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

    private ConverterSelection selectedConverter;
    private Parameter params;
    private Column outputColumn;
    private Column cd;

    @Override
    public void processRow(Row row) throws Exception {
        String toConvert = row.get(cd);
        if (toConvert == null || toConvert.equals("")) {
            getProcessorOutput().emitRow(row);
            return;
        }

        String output = selectedConverter.convert(toConvert);
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
        selectedConverter = chooseConverter(params.processingMode);
    }

    private ConverterSelection chooseConverter(ProcessingMode processingMode) {
        switch(processingMode) {
            case HEXADECIMALTODECIMAL:
                return ConverterSelection.HEXADECIMALTODECIMAL;
            case BINARYTODECIMAL:
                return ConverterSelection.BINARYTODECIMAL;
            case DECIMALTOHEXADECIMAL:
                return ConverterSelection.DECIMALTOHEXADECIMAL;
            case DECIMALTOBINARY:
                return ConverterSelection.DECIMALTOBINARY;
            case HEXADECIMALTOBINARY:
                return ConverterSelection.HEXADECIMALTOBINARY;
            case BINARYTOHEXADECIMAL:
                return ConverterSelection.BINARYTOHEXADECIMAL;
            default:
                throw new IllegalArgumentException("Invalid processing mode: " + processingMode);
        }
    }

    enum ConverterSelection implements Converter {
        BINARYTODECIMAL {
            @Override
            public String convert(String toConvert) {
                return binaryToDecimal(toConvert);
            }
        },
        HEXADECIMALTODECIMAL {
            @Override
            public String convert(String toConvert) {
                return hexadecimalToDecimal(toConvert);
            }
        },
        DECIMALTOBINARY {
            @Override
            public String convert(String toConvert) {
                return decimalToBinary(toConvert);
            }
        },
        DECIMALTOHEXADECIMAL {
            @Override
            public String convert(String toConvert) {
                return decimalToHexadecimal(toConvert);
            }
        },
        HEXADECIMALTOBINARY {
            @Override
            public String convert(String toConvert) {
                return hexadecimalToBinary(toConvert);
            }
        },
        BINARYTOHEXADECIMAL {
            @Override
            public String convert(String toConvert) {
                return binaryToHexadecimal(toConvert);
            }
        }
    }
    interface Converter {
        String convert(String toConvert);
    }
}