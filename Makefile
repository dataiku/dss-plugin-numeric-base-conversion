PLUGIN_VERSION=1.0.0
PLUGIN_ID=base-conversion

all:
	cat plugin.json|json_pp > /dev/null
	ant clean
	rm -rf dist
	ant
	mkdir dist
	zip -r dist/dss-plugin-${PLUGIN_ID}-${PLUGIN_VERSION}.zip js lib plugin.json

reinstall-in-dss: all
	${DIP_HOME}/bin/dku install-plugin dist/dss-plugin-${PLUGIN_ID}-${PLUGIN_VERSION}.zip -u
	${DIP_HOME}/bin/dss restart backend

