package com.dataiku.dip.plugins.numericbaseconversion;

import com.dataiku.dip.PluginEntryPoint;
import com.dataiku.dip.shaker.processors.BaseProcessorsFactory;

public class Plugin extends PluginEntryPoint {
    public void load() throws Exception {
        BaseProcessorsFactory.addProcessor(this, NumericBaseConversion.META);
    }
}
