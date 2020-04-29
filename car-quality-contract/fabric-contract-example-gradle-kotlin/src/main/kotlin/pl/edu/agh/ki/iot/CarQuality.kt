package pl.edu.agh.ki.iot

import org.hyperledger.fabric.contract.annotation.DataType
import org.hyperledger.fabric.contract.annotation.Property
import org.json.JSONObject

@DataType
class CarQuality(@Property val value: Int) {

    fun toJSONString(): String {
        return JSONObject(this).toString()
    }

    companion object {
        fun fromJSONString(json: String): CarQuality {
            val value = JSONObject(json).getInt("value")
            return CarQuality(value)
        }
    }
    
}