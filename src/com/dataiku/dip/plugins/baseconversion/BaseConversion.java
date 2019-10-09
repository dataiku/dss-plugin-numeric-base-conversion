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

    private Pattern HEXA_RE = Pattern.compile("(0x)?([0-9a-fA-F]+)h?");
    private Pattern BIN_RE = Pattern.compile("([0-1]+)b?");
    //private Pattern DECI_RE = Pattern.compile("^([0-9]+).?");
    private Pattern DECI_RE = Pattern.compile("^([0-9]+)(.|,)?[0-9]+$");

    @Override
    public void processRow(Row row) throws Exception {
        String toConvert = row.get(cd);
        if(toConvert == null || toConvert.equals("")){
            getProcessorOutput().emitRow(row);
            return;
        }

        baseConversion(row, toConvert);

        getProcessorOutput().emitRow(row);
        return;
    }

    private void baseConversion(Row row, String toConvert) {
        // 0x3D 3Dh 3D
        // 11011b 11011
        Matcher matcher;
        switch (params.processingMode) {
            case HEXATODECIMAL:
                matcher = HEXA_RE.matcher(toConvert);
                if (matcher.matches()) {
                    Long output;
                    output = Long.parseLong(matcher.group(2), 16);
                    row.put(outputColumn, output);
                }
                break;
            case BINTODECIMAL:
                matcher = BIN_RE.matcher(toConvert);
                if (matcher.matches()) {
                    Long output;
                    output = Long.parseLong(matcher.group(1), 2);
                    row.put(outputColumn, output);
                }
                break;
            case DECIMALTOHEXA:
                matcher = DECI_RE.matcher(toConvert);
                if (matcher.matches()) {
                    String output;
                    output = Long.toHexString(Long.parseLong(matcher.group(1)));
                    row.put(outputColumn, output.toUpperCase());
                }
                break;
            case DECIMALTOBIN:
                matcher = DECI_RE.matcher(toConvert);
                if (matcher.matches()) {
                    String output;
                    output = Long.toHexString(Long.parseLong(matcher.group(1)));
                    row.put(outputColumn, output.toUpperCase());
                }
                break;
            case HEXATOBIN:
                matcher = HEXA_RE.matcher(toConvert);
                if (matcher.matches()) {
                    String output;
                    output = Long.toBinaryString(Long.parseLong(matcher.group(2), 16));
                    row.put(outputColumn, output);
                }
                break;
            case BINTOHEXA:
                matcher = BIN_RE.matcher(toConvert);
                if (matcher.matches()) {
                    String output;
                    output = Long.toHexString(Long.parseLong(matcher.group(1), 2));
                    row.put(outputColumn, output.toUpperCase());
                }
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
    }

}
