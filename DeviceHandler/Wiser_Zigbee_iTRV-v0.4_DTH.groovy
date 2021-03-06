/**
*  Copyright 2019 gszabados
*
*  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License. You may obtain a copy of the License at:
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
*  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
*  for the specific language governing permissions and limitations under the License.
*
*  Wiser Zigbee iRTV
*
*  Version: 0.4
*
*  Author: gszabados
*
*  Date: 2020-02-17
*  
*  Based on ckpt-martin/SmartThings eCozy Zigbee Thermostat DTH
*/

import groovy.json.JsonOutput

metadata {
	definition (name: "Wiser Zigbee iTRV - Update v0.4", namespace: "gszabados", author: "gszabados", mnmn: "SmartThings", vid: "generic-radiator-thermostat") {
		capability "Thermostat"
		capability "Thermostat Mode"
		capability "Refresh"
		capability "Battery"
		capability "Thermostat Heating Setpoint"
		capability "Health Check"
		capability "Temperature Measurement"
		capability "Sensor"
		capability "Configuration"


//		command "modeHeat"
//		command "modeOff"
//		command "modeAuto"
		command "increaseHeatSetpoint"
		command "decreaseHeatSetpoint"

		
        attribute "trvHeatingSetpoint", "number"
        attribute "tempAndSetPoint", "String"
        attribute "anyMessage", "String"
        attribute "proprietaryMessage", "String"
        attribute "uiMessage", "String"
        attribute "motorMessage", "String"
        attribute "batteryMessage", "String"
        attribute "adcMessage", "String"
        attribute "algMessage", "String"
        attribute "lastCheckin", "String"
		attribute "lQI", "number"
		attribute "rSSI", "number"

		
        fingerprint profileId: "0104", endpointId: "01", inClusters: " 0000,0001,0003,0020,0201,0204,0B05,FE03", outClusters: " 0000,0019", manufacturer: "Schneider Electric", model: "iTRV"
	}

	preferences {
		input("unitformat", "enum", title: "What unit format do you want to display temperature in SmartThings? (NOTE: Thermostat displays Celsius regardless.)", options: ["Celsius", "Fahrenheit"], defaultValue: "Celsius", required: false, displayDuringSetup: false)
		//input("lock", "enum", title: "Display Lock?", options: ["No", "Temperature", "Touchscreen"], defaultValue: "No", required: false, displayDuringSetup: false)
		input("tempcal", "enum", title: "Temperature adjustment.", options: ["+2.5", "+2.0", "+1.5", "+1.0", "+0.5", "0", "-0.5", "-1.0", "-1.5", "-2.0", "-2.5"], defaultValue: "0", required: false, displayDuringSetup: false)
	}
	
	
	// simulator metadata
	simulator { }


	tiles(scale : 2) {
		multiAttributeTile(name:"thermostatMulti", type:"thermostat", width:6, height:4, icon:"st.Home.home1", canChangeIcon: true) {
		
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState "default", label:'${currentValue}°', unit:"dC"/*,  backgroundColors:[
                        [value: 0, color: "#153591"],
                        [value: 5, color: "#1e9cbb"],
                        [value: 10, color: "#90d2a7"],
                        [value: 15, color: "#44b621"],
                        [value: 20, color: "#f1d801"],
                        [value: 25, color: "#d04e00"],
                        [value: 30, color: "#bc2323"],
                        [value: 44, color: "#1e9cbb"],
                        [value: 59, color: "#90d2a7"],
                        [value: 74, color: "#44b621"],
                        [value: 84, color: "#f1d801"],
                        [value: 95, color: "#d04e00"],
                        [value: 96, color: "#bc2323"]
                        ]*/
			}

            tileAttribute("device.heatingSetpoint", key: "VALUE_CONTROL", label:'${currentValue}°') {
				attributeState "VALUE_UP", action:"increaseHeatSetpoint"
				attributeState "VALUE_DOWN", action:"decreaseHeatSetpoint"
			}
			
			tileAttribute("device.battery", key: "SECONDARY_CONTROL") {
				attributeState("default", label:'${currentValue}%', unit:"%", icon:"st.arlo.sensor_battery_4")
				attributeState("1%", label:'BATTERY LOW!', icon:"st.arlo.sensor_battery_1")
			}
			
			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
                attributeState "off", backgroundColor:"#1e9cbb", label:'Off'
                attributeState "idle", backgroundColor:"#44b621", label:'Idle', icon:"st.thermostat.heating-cooling-off"
				attributeState "heating", backgroundColor:"#ffa81e", label:'Heating', icon:"st.thermostat.heat"
			}
			
            tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE", supportedStates: "device.supportedThermostatModes") {
				attributeState "auto", label:'${currentValue}', icon:"st.thermostat.heat-auto"
				attributeState "heat", label:"heat", backgroundColor:"#ffa81e", icon:"st.thermostat.heat"
				attributeState "off", label:"off", backgroundColor:"#1e9cbb", icon:"st.thermostat.heating-cooling-off"
			}
            
            
            tileAttribute("device.trvHeatingSetpoint", key: "HEATING_SETPOINT") {
				attributeState("default", label:'${currentValue}', unit:"°C")
            }
		}

		controlTile("thermostatMode", "device.thermostatMode", "enum", width: 2 , height: 2, supportedStates: "device.supportedThermostatModes") {
			state("off", action: "setThermostatMode", label: 'Off', icon: "st.thermostat.heating-cooling-off")
			state("heat", action: "setThermostatMode", label: 'Heat', icon: "st.thermostat.heat")
			state("auto", action:"setThermostatMode", label: 'Auto', icon: "st.thermostat.heat-auto")
		}

       valueTile("heatingInfo", "device.tempAndSetPoint") {
            state "default", label:'${currentValue}', backgroundColor:"#1e9cbb"
            state "off", backgroundColor:"#1e9cbb", label:'${currentValue}'
            state "idle", backgroundColor:"#44b621", label:'${currentValue}'
            state "heating", backgroundColor:"#ffa81e", label:'${currentValue}'
        }

       valueTile("heatingInfoOff", "device.tempAndSetPoint") {
            state "default", label:'${currentValue}', backgroundColor:"#1e9cbb"
            state "off", backgroundColor:"#1e9cbb", label:'${currentValue}'
            state "idle", backgroundColor:"#44b621", label:'${currentValue}'
            state "heating", backgroundColor:"#ffa81e", label:'${currentValue}'
        }

        valueTile("heatingInfoHeating", "device.tempAndSetPoint") {
            state "default", backgroundColor:"#ffa81e", label:'${currentValue}'
        }

        standardTile("heatingState", "device.thermostatOperatingState") {
            state "off", backgroundColor:"#1e9cbb", label:'${currentValue}'
            state "idle", backgroundColor:"#44b621", label:'${currentValue}'
            state "heating", backgroundColor:"#ffa81e", label:'${currentValue}'
        }

		standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat", width: 3, height: 3) {
			state "configure", label:"", action:"configuration.configure", icon:"st.secondary.configure"
		}

		standardTile("refresh", "device.refresh", decoration: "flat", width: 3, height: 3) {
			state "default", action:"refresh", icon:"st.secondary.refresh"
		}

		valueTile("hwver", "device.hwver", decoration: "flat", width: 3, height: 1) {
			state "default", label:'HW Version\n${currentValue}'
		}

		valueTile("swver", "device.swver", decoration: "flat", width: 3, height: 1) {
			state "default", label:'SW Version\n${currentValue}'
		}

		valueTile("lqi", "device.lQI", decoration: "flat", width: 3, height: 1) {
			state "default", label:'LQI: ${currentValue}'
		}

		valueTile("rssi", "device.rSSI", decoration: "flat", width: 3, height: 1) {
			state "default", label:'RSSI: ${currentValue} dBm'
		}

		valueTile("placeholder", "device.placeholder", decoration: "flat", width: 4, height: 1) {
			state "default", label:""
		}

		/* standardTile("setThermostatMode", "device.thermostatMode", decoration: "flat", width: 2, height: 2) {
			state "auto", label:'${currentValue}°', action:"modeOff", icon:"st.thermostat.heat-auto", nextState: "off"
			state "off", action:"modeHeat", icon:"st.thermostat.heating-cooling-off", nextState: "heat"
			state "heat", action:"modeAuto", icon:"st.thermostat.heat", nextState: "auto"
		}
		*/
		
		//main ("thermostatMulti")
		main ("heatingInfo")
	
		details(["thermostatMulti", "hwver", "swver", "lqi", "rssi", /*"placeholder", "setThermostatMode",*/ "configure", "refresh", ])
	}
}



// parse events into attributes
def parse(String description) {
	def result = []
    def newMessageRead = description
    def now = new Date().format("yyyy MMM dd EEE h:mm:ss a", TimeZone.getTimeZone("CET"))

    sendEvent(name: "lastCheckin", value: now, displayed: false)

    log.debug "Parse description $description"

	//Only for testing
    //sendEvent(name: "anyMessage", value: newMessageRead, displayed: false)

	def map = [:]
	if (description?.startsWith("read attr -")) {
		def descMap = parseDescriptionAsMap(description)
		
		log.debug "Desc Map: $descMap"
		
		if (descMap.cluster == "0201" && descMap.attrId == "0000")
		{
			// if (descMap.size == "1C")
			if (descMap.size == "28")
            {
				//map += parseComplexValues(descMap.value)
				//map = parseComplexValues(descMap.value)
				result = parseComplexValues(descMap.value)
            }
            else
            {
				//log.debug "TEMP: $descMap.value"
				map.name = "temperature"
				map.value = getTemperature(descMap.value)
                map.unit = getTemperatureScale()
				if (descMap.value == "8000") // 0x8000 invalid temperature
				{
					map.value = "--"
				}
            }
		}
		
		else if (descMap.cluster == "0001" && descMap.attrId == "0020")
		{
			log.debug "BATTERY VOLTAGE: $descMap.value"
			map.name = "battery"
            map.unit = "%"
			def batteryVoltage = getBatteryVoltage(descMap.value)
			log.debug "BATTERY VOLTAGE: $batteryVoltage"
			if (batteryVoltage < 25)
			{
				map.value = "1"
			}
			else if (batteryVoltage == 25)
			{
				map.value = "20"
			}
			else if (batteryVoltage == 26)
			{
				map.value = "40"
			}
			else if (batteryVoltage == 27)
			{
				map.value = "60"
			}
			else if (batteryVoltage == 28)
			{
				map.value = "80"
			}
			else if (batteryVoltage >= 29)
				{
				map.value = "100"
			}
		}
		
		else if (descMap.cluster == "0000" && descMap.attrId == "0001")
		{
			log.debug "APPLICATION VERSION: $descMap.value"
			map.name = "swver"
			map.value = descMap.value
		}
		
		else if (descMap.cluster == "0000" && descMap.attrId == "0003")
		{
			log.debug "HW VERSION: $descMap.value"
			map.name = "hwver"
			map.value = descMap.value
		}
		
		else if (descMap.cluster == "0201" && descMap.attrId == "0010")
		{
			log.debug "TEMP CALIBRATION: $descMap.value"
		}
		
		else if (descMap.cluster == "0201" && descMap.attrId == "0012")
		{
			log.debug "HEATING SETPOINT: $descMap.value"
			map.name = "trvHeatingSetpoint"
			map.value = getTemperature(descMap.value)
            map.unit = getTemperatureScale()
			if (descMap.value == "8000") //0x8000
			{
				map.value = "--"
			}
            def needMore = zigbee.readAttribute(0x201, 0x001C) + //Read SystemMode
			zigbee.readAttribute(0x201, 0x001E) //Read ThermostatRunningMode
            fireCommand(needMore)
		}
		
		else if (descMap.cluster == "0201" && descMap.attrId == "0008")
		{
			log.debug "THERMOSTAT STATE: $descMap.value"
			
			map.name = "thermostatOperatingState"
			
			if (descMap.value == "00")
			{
				map.value = "off"
			}
			else if (descMap.value < "10")
			{
				map.value = "idle"
			}
			else
			{
				map.value = "heating"
			}
		}
		
		else if (descMap.cluster == "0201" && descMap.attrId == "001C")
		{
			log.debug "THERMOSTAT MODE: $descMap.value"
			map.name = "thermostatMode"
			if (descMap.value == "00")
			{
				map.value = "off"
			}
			else if (descMap.value == "01")
			{
				map.value = "auto"
			}
			else if (descMap.value == "04")
			{
				map.value = "heat"
			}
		}
		
		//LOCK DISPLAY is not part of the Wiser TRV
		/* else if (descMap.cluster == "0204" && descMap.attrId == "0001")
		{
			log.debug "LOCK DISPLAY MODE: $descMap.value"
			map.name = "lockMode"
			if (descMap.value == "00")
			{
				map.value = "unlocked"
			}
			else if (descMap.value == "02")
			{
				map.value = "templock"
			}
			else if (descMap.value == "04")
			{
				map.value = "off"
			}
			else if (descMap.value == "05")
			{
				map.value = "off"
			}
		}*/
        
		else if (descMap.cluster == "FE03" && descMap.attrId == "0020" && descMap.encoding == "42")
		{
			log.debug "Proprietary message: $descMap.value"
			map.name = "proprietaryMessage"
			String messageToDecode = descMap.value.toString()
			StringBuilder str = new StringBuilder();
			
			for (int i = 0; i < messageToDecode.length(); i+=2)
					{
					str.append((char) Integer.parseInt(messageToDecode.substring(i, i + 2), 16))
					}

			String decodedMessage = str.toString()

			//def decodedMessage = messageToDecode.decodeHex()
			
			log.debug "DECODED MESSAGE: $decodedMessage"
			
			map.value = decodedMessage
			map.displayed = "false"
			map.isStateChange = "true"
			
			if (decodedMessage?.startsWith("UI,"))
			{
				map.name = "uiMessage"
				def uiDecodedMessage = decodedMessage.split(',')
				map.value = uiDecodedMessage[1]
				map.displayed = "true"
			}
			
			else if (decodedMessage?.startsWith("MOT,"))
			{
				map.name = "motorMessage"
				def motorDecodedMessage = decodedMessage.split(',')
				map.value = motorDecodedMessage[1]
				map.displayed = "true"
			}
			
			else if (decodedMessage?.startsWith("BAT,"))
			{
				map.name = "batteryMessage"
				def batteryDecodedMessage = decodedMessage.split(',')
				map.value = batteryDecodedMessage[2] + " " + batteryDecodedMessage[1] + " Volt"
				map.displayed = "true"
			}
			
			else if (decodedMessage?.startsWith("ADC,"))
			{
				map.name = "adcMessage"
			}
			
			else if (decodedMessage?.startsWith("ALG,"))
			{
				map.name = "algMessage"
			}
		}
		
		else if (descMap.cluster == "0B05" && descMap.attrId == "011c")
		{
			//if (descMap.size == "10")
			if (descMap.size == "16")
            {
				result = parseLQIandRSSI(descMap.value)
            }
            else
            {
				map.name = "lQI"
				log.debug "LQI: $descMap.value"
				map.value = getLQI(descMap.value)
			}
		}
		
		else if (descMap.cluster == "0B05" && descMap.attrId == "011d")
		{
			map.name = "rSSI"
			log.debug "RSSI: $descMap.value"
			map.value = getRSSI(descMap.value)
		}
	}

	if (map)
	{
		result = createEvent(map)
	}
	
	log.debug "Parse returned $map"
	log.debug "FINAL RESULT: $result"
	
	return result
}

def parseDescriptionAsMap(description) {
	(description - "read attr - ").split(",").inject([:]) { map, param ->
		def nameAndValue = param.split(":")
		map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
	}
}


def getLQI(value) {
	if (value != null)
	{
		log.debug("value $value")
	return Math.round(Integer.parseInt(value, 16))
	}
}

def getRSSI(value) {
	if (value != null)
	{
		log.debug("value $value")
		return hexToSignedInt8(value)
	}
}

def getBatteryVoltage(value) {
	if (value != null)
	{
		log.debug("value $value")
		return Math.round(Integer.parseInt(value, 16))
	}
}

def getTemperature(value) {
	if (value != null)
	{
		log.debug("value $value")
		//def celsius = Integer.parseInt(value.reverse().take(4).reverse(), 16) / 100
		float celsius;
		celsius = hexToSignedInt(value.reverse().take(4).reverse()) / 100
		log.debug("Real TEMP: $celsius")
	
		if (settings.unitformat == "Fahrenheit")
		{
			return Math.round(celsiusToFahrenheit(celsius))
		}
		else
		{
			//return Math.round(celsius)
			return celsius
		}
	}
}

def getSetPoint(value) {
	if (value != null)
	{
		log.debug("value $value")
		float celsius;
		if (value.reverse().substring(4,8).reverse() == "0012" && value.reverse().substring(8,10).reverse() == "29")
		{
			celsius = hexToSignedInt(value.reverse().substring(10,14).reverse()) / 100
		}
		
		log.debug("SETPOINT: $celsius")

		def radiatorTemperature = celsius
		def currentTemperature = currentDouble("trvHeatingSetpoint")
		def nextTemperature = currentDouble("heatingSetpoint")

		if (nextTemperature != 0 && nextTemperature != currentTemperature)
		{
			log.debug "Sending new temperature ${nextTemperature}"
			state.lastSentTemperature = nextTemperature
			setValveHeatingSetpoint()
		}

		def deviceTempMap = [name: "trvHeatingSetpoint", value: radiatorTemperature, unit: getTemperatureScale()]
		
		if (radiatorTemperature != currentTemperature)
		{
		
			// The radiator temperature has changed. Why?
			// Is it because the app told it to change or is it coz it was done manually
			
			if(state.lastSentTemperature == radiatorTemperature)
			{
				def thermostatMode = device.currentValue("thermostatMode")
				
				// radiator is off
				if (thermostatMode == "off")
				{
                	log.debug "Thermostat mode is off, temperature changed to ${radiatorTemperature}"
					deviceTempMap.descriptionText = "Thermostat mode is off, temperature changed to ${radiatorTemperature}"
				}
			
				// it's the app! raise event heatingSetpoint, with desc App
				else
				{
					log.debug "Temperature changed by app to ${radiatorTemperature}"
                    deviceTempMap.descriptionText = "Temperature changed by app to ${radiatorTemperature}"
				}
			}
			
			else
			{
				// It's manual? raise event heatingSetpoint, with desc Manual
				// And I think set the next to be the manual setting too. All aligned.
				deviceTempMap.descriptionText = "Temperature changed manually to ${radiatorTemperature}"
                log.debug "Temperature changed manually to ${radiatorTemperature}"
				state.lastSentTemperature = radiatorTemperature
				sendEvent(name:"heatingSetpoint", value: radiatorTemperature, unit: getTemperatureScale(), displayed: false)
			}
		}
		if (settings.unitformat == "Fahrenheit")
		{
			return Math.round(celsiusToFahrenheit(celsius))
		}
			
		else
		{
			// return Math.round(celsius)
        	log.debug "sending back $celsius"
			return celsius
		}
	}
}

def getDemand(value) {
	if (value != null)
	{
		log.debug("value $value")
		def piHeatDemand
		int piHeatDemandInt;

		if (value.reverse().substring(14,18).reverse() == "0008" && value.reverse().substring(18,20).reverse() == "20")
		{
			piHeatDemand = (value.reverse().substring(20,22).reverse())
			piHeatDemandInt = hexToInt(piHeatDemand)
		}
		
		log.debug "DEMAND: $piHeatDemand,  $piHeatDemandInt %"
		return piHeatDemand
	}
}

def getOperatingState(value) {
	if (value != null)
	{
		log.debug("value $value")
		def piHeatDemand = value
		def thermostatOperatingState
		
		if (piHeatDemand == "00")
		{
			thermostatOperatingState = "off"
		}
		
		else if (piHeatDemand < "10")
		{
			thermostatOperatingState = "idle"
		}
		
		else
		{
			thermostatOperatingState = "heating"
		}
		
		log.debug "THERMOSTAT STATE: $thermostatOperatingState"
		return thermostatOperatingState
	}
}

def quickSetHeat(degrees) {
	setHeatingSetpoint(degrees.toDouble())
}

def setHeatingSetpoint(Double degrees) {
	log.debug "Storing temperature for next wake ${degrees}"
	sendEvent(name:"heatingSetpoint", value: degrees.round(1), unit: getTemperatureScale(), descriptionText: "Next heating setpoint is ${degrees}")
}

def setValveHeatingSetpoint() {
	//if (degrees != null) {
		/*def degreesInteger = Math.round(degrees)
		int temp;
		temp = (Math.round(degrees * 2)) / 2*/

		//log.debug "setHeatingSetpoint({$temp} ${temperatureScale})"
		log.debug "New temperature check. Next: ${device.currentValue("heatingSetpoint")} vs Current: ${device.currentValue("trvHeatingSetpoint")}"

		def trvHeatingSetpoint = currentDouble("trvHeatingSetpoint")
		def heatingSetpoint = currentDouble("heatingSetpoint")
		def thermostatMode = device.currentValue("thermostatMode")

		log.debug "Thermostat mode is ${thermostatMode}"
		/*if (thermostatMode == "off")
		{
			heatingSetpoint = quickOffTemperature ?: fromCelsiusToLocal(4)
		}*/

		if (heatingSetpoint != 0 && heatingSetpoint != trvHeatingSetpoint)
		{
			log.debug "Sending new temperature ${heatingSetpoint}"
			state.lastSentTemperature = heatingSetpoint
			def celsius = (settings.unitformat == "Fahrenheit") ? (fahrenheitToCelsius(temp)).round : temp
			def cmds =
					zigbee.writeAttribute(0x201, 0x12, 0x29, hex(heatingSetpoint * 100)) //+
					//zigbee.readAttribute(0x201, 0x12) + //Read Heat Setpoint
					//zigbee.readAttribute(0x201, 0x08) //Read PI Heat demand
			fireCommand(cmds)
		// }
	}
}

def increaseHeatSetpoint() {
    def currentMode = device.currentState("thermostatMode")?.value
    if (currentMode != "off")
    {
		float currentSetpoint = device.currentValue("heatingSetpoint")
		float maxSetpoint
		float step
		
		if (settings.unitformat == "Fahrenheit")
		{
		//Maximum Setpoint in Fahrenheit
			maxSetpoint = 86
			step = 1
		}
		else
		{
		//Maximum Setpoint in Celsius
			maxSetpoint = 30
			step = 0.5
		}

		if (currentSetpoint < maxSetpoint)
		{
			currentSetpoint = currentSetpoint + step
			quickSetHeat(currentSetpoint)
		}
    }
}

def decreaseHeatSetpoint() {
    def currentMode = device.currentState("thermostatMode")?.value
    if (currentMode != "off")
	{
		float currentSetpoint = device.currentValue("heatingSetpoint")
		float minSetpoint
		float step

		if (settings.unitformat == "Fahrenheit")
		{
			minSetpoint = 41
			step = 1
		}
		else
		{
			minSetpoint = 5
			step = 0.5
		}

		if (currentSetpoint > minSetpoint)
		{
			currentSetpoint = currentSetpoint - step
			quickSetHeat(currentSetpoint)
		}
    }
}

def setThermostatMode(String mode) {
	log.debug "Setting Thermostat Mode"
    log.debug "received mode is $mode (supported ${state.supportedThermostatModes})"
    	def modeValue = 0x00
	if (state.supportedThermostatModes?.contains(mode)) {
		switch (mode) {
			case "auto":
				modeValue = 0x01
				break
			case "heat":
				modeValue = 0x04
				break
			case "off":
				modeValue = 0x00
				break
		}
	} else {
		log.debug "Unsupported mode ${mode}"
	}
    
    [
			zigbee.writeAttribute(0x201, 0x001C, 0x30, modeValue),
			"delay 2000",
			zigbee.readAttribute(0x201, 0x001C)
	]
    
    
	/*if (mode == "heat")
    {
    	log.debug "sending heatMode"
		zigbee.writeAttribute(0x201, 0x001C, 0x30, 0x04)
    }
    else if (mode == "auto")
    {  	
    	log.debug "sending autoMode"
		zigbee.writeAttribute(0x201, 0x001C, 0x30, 0x01)
    }
    else if (mode == "off")
    {  	
    	log.debug "sending offMode"	
		zigbee.writeAttribute(0x201, 0x001C, 0x30, 0x00)
    }*/
}

def heat() {
	log.debug "modeHeat"
    sendEvent(name:"thermostatMode", value:"Heat", displayed: true)
	setThermostatMode("heat")
	}

def off() {
	log.debug "modeOff"
	sendEvent(name:"thermostatMode", value:"Off", displayed: true)
	setThermostatMode("off")
}

def auto() {
	log.debug "modeAuto"
	sendEvent(name:"thermostatMode", value:"Auto", displayed: true)
	setThermostatMode("auto")
}

def configure() {
	def cmds =
			//Cluster ID (0x0201 = Thermostat Cluster), Attribute ID, Data Type, Payload (Min report, Max report, On change trigger)
			zigbee.configureReporting(0x0201, 0x0008, 0x20, 300, 7200, 0x05) +   //Attribute ID 0x0008 = pi heating demand, Data Type: U8BIT
			//zigbee.configureReporting(0x0201, 0x0000, 0x29, 30, 0, 0x0064) +   //Attribute ID 0x0000 = local temperature, Data Type: S16BIT
			//zigbee.configureReporting(0x0201, 0x0012, 0x29, 30, 0, 0x0064) +   //Attribute ID 0x0012 = occupied heat setpoint, Data Type: S16BIT
			zigbee.configureReporting(0x0201, 0x0000, 0x29, 90, 600, 0x0064) +   //Attribute ID 0x0000 = local temperature, Data Type: S16BIT
			zigbee.configureReporting(0x0201, 0x0012, 0x29, 90, 600, 0x0064) +   //Attribute ID 0x0012 = occupied heat setpoint, Data Type: S16BIT
			//zigbee.configureReporting(0x0201, 0x001C, 0x30, 1, 0, 1)           //Attribute ID 0x001C = system mode, Data Type: 8 bits enum
			zigbee.configureReporting(0x0201, 0x001C, 0x30, 90, 600, 1)          //Attribute ID 0x001C = system mode, Data Type: 8 bits enum

			//Cluster ID (0x0001 = Power)
			zigbee.configureReporting(0x0001, 0x0020, 0x20, 600, 21600, 0x01)    //Attribute ID 0x0020 = battery voltage, Data Type: U8BIT

	log.info "configure() --- cmds: $cmds"
	return refresh() + cmds
}

def refresh() {
	def cmds =
			//Read the configured variables
			zigbee.readAttribute(0x201, 0x0008) + //Read PIHeatingDemand
			zigbee.readAttribute(0x201, 0x0000) + //Read LocalTemperature
			zigbee.readAttribute(0x201, 0x0012) + //Read OccupiedHeatingSetpoint
			zigbee.readAttribute(0x201, 0x001C) + //Read SystemMode
			zigbee.readAttribute(0x201, 0x001E) + //Read ThermostatRunningMode
			zigbee.readAttribute(0x201, 0x0010) + //Read LocalTemperatureCalibration
			zigbee.readAttribute(0x001, 0x0020) + //Read BatteryVoltage
			zigbee.readAttribute(0x000, 0x0003) + //Read HW Version
			zigbee.readAttribute(0x000, 0x0001)   //Read Application Version
			// zigbee.readAttribute(0x204, 0x0001) + //Read KeypadLockout
			
			// Other Available Attributes - DISABLED!!!
			
			/* zigbee.readAttribute(0x000, 0x0000) + //Read ZCLVersion
			zigbee.readAttribute(0x000, 0x0001) + //Read ApplicationVersion
			zigbee.readAttribute(0x000, 0x0002) + //Read StackVersion
			zigbee.readAttribute(0x000, 0x0003) + //Read HWVersion
			zigbee.readAttribute(0x000, 0x0004) + //Read ManufacturerName
			zigbee.readAttribute(0x000, 0x0005) + //Read ModelIdentifier
			zigbee.readAttribute(0x000, 0x0006) + //Read DateCode
			zigbee.readAttribute(0x000, 0x0007) + //Read PowerSource
			zigbee.readAttribute(0x000, 0x0010) + //Read LocationDescription
			zigbee.readAttribute(0x000, 0x0011) + //Read PhysicalEnvironment
			zigbee.readAttribute(0x000, 0x0012) + //Read DeviceEnabled
			zigbee.readAttribute(0x000, 0x0014) + //Read DisableLocalConfig
			zigbee.readAttribute(0x020, 0x0000) + //Read Check-inInterval
			zigbee.readAttribute(0x020, 0x0001) + //Read LongPollInterval
			zigbee.readAttribute(0x020, 0x0002) + //Read ShortPollInterval
			zigbee.readAttribute(0x020, 0x0003) + //Read FastPollTimeout
			zigbee.readAttribute(0x020, 0x0004) + //Read Check-inIntervalMin
			zigbee.readAttribute(0x020, 0x0005) + //Read LongPollIntervalMin
			zigbee.readAttribute(0x020, 0x0006) + //Read FastPollTimeoutMax
			zigbee.readAttribute(0x00a, 0x0000) + //Read Time
			zigbee.readAttribute(0x00a, 0x0001) + //Read TimeStatus
			zigbee.readAttribute(0x00a, 0x0002) + //Read TimeZone
			zigbee.readAttribute(0x00a, 0x0003) + //Read DstStart
			zigbee.readAttribute(0x00a, 0x0004) + //Read DstEnd
			zigbee.readAttribute(0x00a, 0x0005) + //Read DstShift
			zigbee.readAttribute(0x00a, 0x0006) + //Read StandardTime
			zigbee.readAttribute(0x00a, 0x0007) + //Read LocalTime
			zigbee.readAttribute(0x00a, 0x0008) + //Read LastSetTime
			zigbee.readAttribute(0x00a, 0x0009) + //Read ValidUntilTime
			zigbee.readAttribute(0x001, 0x0030) + //Read BatteryManufacturer
			zigbee.readAttribute(0x001, 0x0031) + //Read BatterySize
			zigbee.readAttribute(0x001, 0x0032) + //Read BatteryAHrRating
			zigbee.readAttribute(0x001, 0x0033) + //Read BatteryQuantity
			zigbee.readAttribute(0x001, 0x0034) + //Read BatteryRatedVoltage
			zigbee.readAttribute(0x001, 0x0035) + //Read BatteryAlarmMask
			zigbee.readAttribute(0x001, 0x0036) + //Read BatteryVoltageMinThreshold
			zigbee.readAttribute(0x201, 0x0030) + //Read SetpointChangeSource
			zigbee.readAttribute(0x201, 0x0032) + //Read SetpointChangeSourceTimestamp
			zigbee.readAttribute(0x201, 0x0003) + //Read AbsMinHeatSetpointLimit
			zigbee.readAttribute(0x201, 0x0004) + //Read AbsMaxHeatSetpointLimit
			zigbee.readAttribute(0x201, 0x0015) + //Read MinHeatSetpointLimit
			zigbee.readAttribute(0x201, 0x0016) + //Read MaxHeatSetpointLimit
			zigbee.readAttribute(0x201, 0x0019) + //Read MinSetpointDeadBand
			zigbee.readAttribute(0x201, 0x001A) + //Read RemoteSensing
			zigbee.readAttribute(0x201, 0x001B) + //Read ControlSequenceOfOperation
			zigbee.readAttribute(0x201, 0x0020) + //Read StartOfWeek
			zigbee.readAttribute(0x201, 0x0021) + //Read NumberOfWeeklyTransitions
			zigbee.readAttribute(0x201, 0x0022) + //Read NumberOfDailyTransitions
			zigbee.readAttribute(0x201, 0x0023) + //Read TemperatureSetpointHold
			zigbee.readAttribute(0x201, 0x0031) //Read SetpointChangeAmount
			*/

    log.info "refresh() --- cmds: $cmds"
	return cmds
}

def ping() {
	log.debug "ping called"
	refresh()
}

def installed() {
	log.debug "installed called"
	sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])
	    
	state.supportedThermostatModes = ["auto", "heat", "off"]
	
	sendEvent(name: "supportedThermostatModes", value: JsonOutput.toJson(state.supportedThermostatModes), displayed: false)
	//sendEvent(name: "heatingSetpointRange", value: heatingSetpointRange, displayed: false)
}

def updated() {
	log.debug "updated called"
	def tempmode = 0x00
	def lockmode = 0x00
	def tempadjust = 0x00
	def windowmode = 0x01
	def firemode = 0x00
	
    if (device.currentValue("checkInterval") != null)
    {
    	log.debug "It was already installed"
        installed()
    }
    else
    {
    	installed()
    }
	/* log.info "lock : $settings.lock"
	
	if (settings.lock == "Temperature")
	{
		log.info "Temperature lock selected"
		lockmode = 0x02
	}
	else if (settings.lock == "Touchscreen")
	{
		log.info "Touchscreen lock selected"
		lockmode = 0x05
	}
	else
	{
		log.info "No lock selected"
		lockmode = 0x00
		settings.lock = "No"
	}
	*/
	
	log.info "unitformat : $settings.unitformat"
	
	if (settings.unitformat == "Fahrenheit")
	{
		log.info "Fahrenheit selected"
	}
	else
	{
		log.info "Celsius set"
		settings.unitformat = "Celsius"
	}

	log.info "tempcal : $settings.tempcal"
	
	if (settings.tempcal == "+2.5")
	{
		log.info "Temperature adjusted +2.5 degrees"
		tempadjust = 0x19
	}
	else if (settings.tempcal == "+2.0")
	{
		log.info "Temperature adjusted +2.0 degrees"
		tempadjust = 0x14
	}
	else if (settings.tempcal == "+1.5")
	{
		log.info "Temperature adjusted +1.5 degrees"
		tempadjust = 0x0f
	}
	else if (settings.tempcal == "+1.0")
	{
		log.info "Temperature adjusted +1.0 degrees"
		tempadjust = 0x0a
	}
	else if (settings.tempcal == "+0.5")
	{
		log.info "Temperature adjusted +0.5 degrees"
		tempadjust = 0x05
	}
	else if (settings.tempcal == "-0.5")
	{
		log.info "Temperature adjusted -0.5 degrees"
		tempadjust = 0xd3
	}
	else if (settings.tempcal == "-1.0")
	{
		log.info "Temperature adjusted -1.0 degrees"
		tempadjust = 0xd8
	}
	else if (settings.tempcal == "-1.5")
	{
		log.info "Temperature adjusted -1.5 degrees"
		tempadjust = 0xed
	}
	else if (settings.tempcal == "-2.0")
	{
		log.info "Temperature adjusted -2.0 degrees"
		tempadjust = 0xe2
	}
	else if (settings.tempcal == "-2.5")
	{
		log.info "Temperature adjusted -2.5 degrees"
		tempadjust = 0xe7
	}
	else
	{
		log.info "No temperature adjustment"
		tempadjust = 0x00
		settings.tempcal = "0"
	}

	log.info "openwindow detect : $settings.openwindow"
	
	if (settings.openwindow == "No")
	{
		log.info "Open Window Detection disabled"
	}
	else
	{
		log.info "Open Window Detection enabled"
		settings.openwindow = "Yes"
	}

	/* log.info "firedetect : $settings.firedetect"
	if (settings.firedetect == "Yes")
	{
		log.info "Fire Detection enabled"
	}
	else
	{
		log.info "Fire Detection disabled"
		settings.firedetect = "No"
	}
	*/
    
	def cmds =
			// zigbee.writeAttribute(0x204, 0x0001, 0x30, lockmode) +  // Lock Mode not part of Wiser TRV
			// zigbee.readAttribute(0x204, 0x0001) +				   // Lock Mode not part of Wiser TRV
			zigbee.writeAttribute(0x201, 0x0010, 0x28, tempadjust) +
			zigbee.readAttribute(0x201, 0x0010)

	def radiatorTemperature = currentDouble("trvHeatingSetpoint")
	def nextTemperature = currentDouble("heatingSetpoint")


	if(nextTemperature == 0)
	{
		// initialise the heatingSetpoint, on the very first time we install and get the devices temp
		state.lastSentTemperature = radiatorTemperature
		sendEvent(name:"heatingSetpoint", value: radiatorTemperature, unit: getTemperatureScale(), displayed: true)
	}

	log.info "updated() --- cmds: $cmds"
	fireCommand(cmds)
}

private fireCommand(List commands) {
	if (commands != null && commands.size() > 0)
	{
		log.trace("Executing commands:" + commands)
		for (String value : commands)
		{
			sendHubCommand([value].collect {new physicalgraph.device.HubAction(it)})
		}
	}
}

private hex(value) {
	new BigInteger(Math.round(value).toString()).toString(16)
}

private List<Map> parseComplexValues(attrData) {
	def results = []

	log.debug "$attrData"
	def temperature = getTemperature(attrData)
	log.debug "TEMP: $temperature"
	//02200008089829001209d8
	/*if (attrData.reverse().substring(4,8).reverse() == "0012" && attrData.reverse().substring(8,10).reverse() == "29")
	{
		def heatSetPoint = hexToSignedInt(attrData.reverse().substring(10,14).reverse()) / 100
		log.debug "SETPOINT: $heatSetPoint"
	}*/

	def heatSetPoint = getSetPoint(attrData)

	def piHeatDemand = getDemand(attrData)

	def thermostatOperatingState = getOperatingState(piHeatDemand)

	log.debug "TEMP: $temperature SETPOINT: $heatSetPoint, DEMAND: $piHeatDemand, THERMOSTAT STATE: $thermostatOperatingState"

	if ([temperature, heatSetPoint, piHeatDemand].any { it == null })
	{
		return []
	}

    def tempAndSetPointText = "" + temperature + "° | " + heatSetPoint + "°"

	results << [
		name           : "temperature",
		value          : temperature,
		]
    
	results << [
		name           : "thermostatOperatingState",
		value          : thermostatOperatingState,
	]
    
	results << [
		name           : "trvHeatingSetpoint",
		value          : heatSetPoint,
	]

    results << [
		name           : "tempAndSetPoint",
		value          : tempAndSetPointText,
    ]

    def temperatureEvent = createEvent(name: "temperature", value: temperature, unit: getTemperatureScale())
    def operatingStateEvent = createEvent(name: "thermostatOperatingState", value: thermostatOperatingState)
    def setPointEvent = createEvent(name: "trvHeatingSetpoint", value: heatSetPoint, unit: getTemperatureScale())
    def tempAndSetPointEvent = createEvent(name: "tempAndSetPoint", value: tempAndSetPointText, displayed: "false")
	
	log.debug "RESULT: $results"
	
    return [temperatureEvent, operatingStateEvent, setPointEvent, tempAndSetPointEvent]
}

private List<Map> parseLQIandRSSI(attrData) {
	def results = []
	
	log.debug "$attrData"
	
	def lQI = hexToInt(attrData.reverse().take(2).reverse())
    log.debug("LQI: $lQI")
	int rSSI
	
	if (attrData.reverse().substring(2,6).reverse() == "011d" && attrData.reverse().substring(6,8).reverse() == "28")
	{
		rSSI = hexToSignedInt8(attrData.reverse().substring(8,10).reverse())
		log.debug("if")
    }
    log.debug("RSSI: $rSSI")
	
	if ([lQI, rSSI].any { it == null })
	{
		return []
	}

	results << [
		name           : "lQI",
		value          : lQI,
	]

    results << [
		name           : "rSSI",
		value          : rSSI,
	]
	
	def lQIEvent = createEvent(name: "lQI", value: lQI)
    def rSSIEvent = createEvent(name: "rSSI", value: rSSI)

	log.debug "RESULT: $results"

    return [lQIEvent, rSSIEvent]
}



private hexToSignedInt(hexVal) {
	if (!hexVal)
	{
		return null
	}

	def unsignedVal = hexToInt(hexVal)
	unsignedVal > 32767 ? unsignedVal - 65536 : unsignedVal
}

private hexToInt(value) {
	new BigInteger(value, 16)
}

private hexToSignedInt8(hexVal) {
	if (!hexVal)
	{
		return null
	}

	def unsignedVal = hexToInt(hexVal)
	unsignedVal > 127 ? unsignedVal - 256 : unsignedVal
}


private currentDouble(attributeName){
	if (device.currentValue(attributeName))
	{
		return device.currentValue(attributeName).doubleValue()
	}
	else
	{
		return 0d
	}
}
