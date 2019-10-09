(function() {
    'use strict';

    var injector = angular.element("body").injector();
    var ShakerProcessorsInfo = injector.get("ShakerProcessorsInfo");

    var conversionModes = {
        "BINTODECIMAL": "binary to decimal",
        "HEXATODECIMAL": "hexa to decimal",
        "DECIMALTOBIN": "decimal to binary",
        "DECIMALTOHEXA": "decimal to hexa",
        "HEXATOBIN": "hexa to binary",
        "BINTOHEXA": "binary to hexa"
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
