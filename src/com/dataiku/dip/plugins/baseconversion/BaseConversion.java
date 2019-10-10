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
        BINTODECIMAL {
            @Override
            public String getLabel() {
                return "Binary to Decimal";
            }
        },
        HEXATODECIMAL {
            @Override
            public String getLabel() {
                return "Hexadecimal to Decimal";
            }
        },
        DECIMALTOBIN {
            @Override
            public String getLabel() {
                return "Decimal to Binary";
            }
        },
        DECIMALTOHEXA {
            @Override
            public String getLabel() {
                return "Decimal to Hexadecimal";
            }
        },
        HEXATOBIN {
            @Override
            public String getLabel() {
                return "Hexadecimal to Binary";
            }
        },
        BINTOHEXA {
            @Override
            public String getLabel() {
                return "Binary to Hexadecimal";
            }
        }
    };
    public enum ConverterSelection implements Converter {
        BINTODECIMAL {
            @Override
            public String getConverted(String toConvert) {
                return binToDecimal(toConvert);
            }
        },
        HEXATODECIMAL {
            @Override
            public String getConverted(String toConvert) {
                return hexaToDecimal(toConvert);
            }
        },
        DECIMALTOBIN {
            @Override
            public String getConverted(String toConvert) {
                return decimalToBin(toConvert);
            }
        },
        DECIMALTOHEXA {
            @Override
            public String getConverted(String toConvert) {
                return decimalToHexa(toConvert);
            }
        },
        HEXATOBIN {
            @Override
            public String getConverted(String toConvert) {
                return hexaToBin(toConvert);
            }
        },
        BINTOHEXA {
            @Override
            public String getConverted(String toConvert) {
                return binToHexa(toConvert);
            }
        }
    }

    ConverterSelection selectedConverter;

    public static String binToDecimal(String toConvert){
        String output = "";
        Matcher matcher = BIN_RE.matcher(toConvert);
        if (matcher.matches()) {
            BigInteger input = new BigInteger(matcher.group(1), 2);
            output = input.toString(10);
        }
        return output;
    }

    public static String hexaToDecimal(String toConvert){
        String output = "";
        Matcher matcher = HEXA_RE.matcher(toConvert);
        if (matcher.matches()) {
            BigInteger input = new BigInteger(matcher.group(2), 16);
            output = input.toString(10);
        }
        return output;
    }

    public static String decimalToBin(String toConvert){
        String output = "";
        Matcher matcher = DECI_RE.matcher(toConvert);
        if (matcher.matches()) {
            BigInteger input = new BigInteger(matcher.group(1), 10);
            output = input.toString(2);
        }
        return output;
    }

    public static String decimalToHexa(String toConvert){
        String output = "";
        Matcher matcher = DECI_RE.matcher(toConvert);
        if (matcher.matches()) {
            BigInteger input = new BigInteger(matcher.group(1), 10);
            output = input.toString(16).toUpperCase();
        }
        return output;
    }

    public static String hexaToBin(String toConvert){
        String output = "";
        Matcher matcher = HEXA_RE.matcher(toConvert);
        if (matcher.matches()) {
            BigInteger input = new BigInteger(matcher.group(2), 16);
            output = input.toString(2);
        }
        return output;
    }

    public static String binToHexa(String toConvert){
        String output = "";
        Matcher matcher = BIN_RE.matcher(toConvert);
        if (matcher.matches()) {
            BigInteger input = new BigInteger(matcher.group(1), 2);
            output = input.toString(16).toUpperCase();
        }
        return output;
    }

    public static final ProcessorMeta<BaseConversion, Parameter> META = new ProcessorMeta<BaseConversion, Parameter>() {

        @Override
        public String getName() {
            return "BaseConversion";
        }

        @Override
        public String getDocPage(){
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
                    .withParam(ParamDesc.advancedSelect("processingMode","Conversion", "", ProcessingMode.class).withDefaultValue(ProcessingMode.BINTODECIMAL))
                    .withMNESParam("outputColumn", "Output column");
        }

        @Override
        public BaseConversion build(BaseConversion.Parameter parameter) throws Exception {
            return new BaseConversion(parameter);
        }
    };

    Parameter params;
    public BaseConversion(Parameter params) {
        this.params = params;
    }

    private Column outputColumn, cd;

    private static Pattern HEXA_RE = Pattern.compile("(0x)?([0-9a-fA-F]+)h?");
    private static Pattern BIN_RE = Pattern.compile("([0-1]+)b?");
    private static Pattern DECI_RE = Pattern.compile("^([0-9]+)(\\.|,)?[0-9]*$");

    @Override
    public void processRow(Row row) throws Exception {
        String toConvert = row.get(cd);
        if(toConvert == null || toConvert.equals("")){
            getProcessorOutput().emitRow(row);
            return;
        }

        String output;
        output = selectedConverter.getConverted(toConvert);
        if (output != "") {
            row.put(outputColumn, output);
        }

        getProcessorOutput().emitRow(row);
        return;
    }

    private void setConverter() {
        switch(params.processingMode) {
            case HEXATODECIMAL:
                selectedConverter = ConverterSelection.HEXATODECIMAL;
                break;
            case BINTODECIMAL:
                selectedConverter = ConverterSelection.BINTODECIMAL;
                break;
            case DECIMALTOHEXA:
                selectedConverter = ConverterSelection.DECIMALTOHEXA;
                break;
            case DECIMALTOBIN:
                selectedConverter = ConverterSelection.DECIMALTOBIN;
                break;
            case HEXATOBIN:
                selectedConverter = ConverterSelection.HEXATOBIN;
                break;
            case BINTOHEXA:
                selectedConverter = ConverterSelection.BINTOHEXA;
                break;
        }
    }

    @Override
    public void postProcess() throws Exception {
        getProcessorOutput().lastRowEmitted();
    }

    @Override
    public void init() throws Exception {
        cd = getCf().column(params.inputColumn, ProcessorRole.INPUT_COLUMN);
        outputColumn = getCf().column(params.outputColumn, ProcessorRole.OUTPUT_COLUMN);
        setConverter();
    }

}
abstract interface Converter {
    public String getConverted(String toConvert);
}