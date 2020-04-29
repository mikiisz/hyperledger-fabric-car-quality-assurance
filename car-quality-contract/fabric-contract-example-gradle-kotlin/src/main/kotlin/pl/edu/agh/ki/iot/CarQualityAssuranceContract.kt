package pl.edu.agh.ki.iot

import org.hyperledger.fabric.contract.Context
import org.hyperledger.fabric.contract.ContractInterface
import org.hyperledger.fabric.contract.annotation.Contact
import org.hyperledger.fabric.contract.annotation.Contract
import org.hyperledger.fabric.contract.annotation.Default
import org.hyperledger.fabric.contract.annotation.Info
import org.hyperledger.fabric.contract.annotation.Transaction

@Contract(name = "CarQualityAssuranceContract",
    info = Info(title = "Car Quality Assurance Contract",
                version = "0.0.1",
                contact = Contact(url = "https://mikiisz.github.io/#75711f")))
@Default
class CarQualityAssuranceContract : ContractInterface {

    @Transaction
    fun carQualityExists(ctx: Context, id: String): Boolean {
        val buffer = ctx.stub.getState(id)
        return (buffer != null && buffer.isNotEmpty())
    }

    @Transaction
    fun createCarQuality(ctx: Context, id: String, value: Int) {
        val exists = carQualityExists(ctx, id)
        if (exists) {
            throw RuntimeException("Quality already exists for car $id")
        } else if (value < 0 || value > 100) {
            throw RuntimeException("Quality value has to be a percentage value [1-100]")
        }
        val quality = CarQuality(value)
        ctx.stub.putState(id, quality.toJSONString().toByteArray(Charsets.UTF_8))
    }

    @Transaction
    fun readCarQuality(ctx: Context, id: String): CarQuality {
        val exists = carQualityExists(ctx, id)
        if (!exists) {
            throw RuntimeException("Quality already exists for car $id")
        }
        return CarQuality.fromJSONString(ctx.stub.getState(id).toString(Charsets.UTF_8))
    }

    @Transaction
    fun updateCarQuality(ctx: Context, id: String, newValue: Int) {
        if (newValue < 0 || newValue > 100) {
            throw RuntimeException("Quality value has to be a percentage value [1-100]")
        }
        val carQuality = readCarQuality(ctx, id)
        val newQuality = CarQuality(newValue)
        ctx.stub.putState(id, newQuality.toJSONString().toByteArray(Charsets.UTF_8))
    }

    @Transaction
    fun deleteCarQuality(ctx: Context, id: String) {
        val exists = carQualityExists(ctx, id)
        if (!exists) {
            throw RuntimeException("Quality doesn't exist for car $id")
        }
        ctx.stub.delState(id)
    }

}
