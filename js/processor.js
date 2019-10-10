(function() {
    'use strict';

    var injector = angular.element("body").injector();
    var ShakerProcessorsInfo = injector.get("ShakerProcessorsInfo");

    var conversionModes = {
        "BINARYTODECIMAL": "binary to decimal",
        "HEXADECIMALTODECIMAL": "hexa to decimal",
        "DECIMALTOBINARY": "decimal to binary",
        "DECIMALTOHEXADECIMAL": "decimal to hexa",
        "HEXADECIMALTOBINARY": "hexa to binary",
        "BINARYTOHEXADECIMAL": "binary to hexa"
    }

    var getDescription = function(params) {
        if (!params["processingMode"] || params["processingMode"].length == 0) return null;
        if (params["processingMode"] in conversionModes) {
            return " from " +  conversionModes[params["processingMode"]]
        } else {
            return null;
        }
    }

    ShakerProcessorsInfo.map["BaseConversion"] = {
        "description": function(type, params) {
            if (!params["inputColumn"] || !params["outputColumn"] || params["inputColumn"].length == 0 || params["outputColumn"].length == 0) return null;
            return "Convert data in column <strong>{0}</strong>".format(sanitize(params["inputColumn"])) + getDescription(params);
        },
        "icon": "icon-superscript"
    }

})();
