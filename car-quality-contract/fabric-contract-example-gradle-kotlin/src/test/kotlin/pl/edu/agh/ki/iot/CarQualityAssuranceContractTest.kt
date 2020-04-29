package pl.edu.agh.ki.iot

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.nhaarman.mockitokotlin2.*

import org.hyperledger.fabric.contract.Context
import org.hyperledger.fabric.shim.ChaincodeStub

class CarQualityAssuranceContractTest {

    lateinit var ctx: Context
    lateinit var stub: ChaincodeStub

    @BeforeEach
    fun beforeEach() {
        ctx = mock()
        stub = mock()
        whenever(ctx.stub).thenReturn(stub)
        whenever(stub.getState("1001")).thenReturn("{\"value\":1}".toByteArray(Charsets.UTF_8))
        whenever(stub.getState("1002")).thenReturn("{\"value\":2}".toByteArray(Charsets.UTF_8))
    }

    @Nested
    inner class carQualityExists {

        @Test
        fun `should return true for a car quality`() {
            val contract = CarQualityAssuranceContract()
            val result = contract.carQualityExists(ctx, "1001")
            assertTrue(result)
        }

        @Test
        fun `should return false for a car quality that does not exist (no key)`() {
            val contract = CarQualityAssuranceContract()
            val result = contract.carQualityExists(ctx, "1003")
            assertFalse(result)
        }

        @Test
        fun `should return false for a car quality that does not exist (no data)`() {
            val contract = CarQualityAssuranceContract()
            whenever(stub.getState("1003")).thenReturn(ByteArray(0))
            val result = contract.carQualityExists(ctx, "1003")
            assertFalse(result)
        }

    }

    @Nested
    inner class createCarQuality {

        @Test
        fun `should create a car quality`() {
            val contract = CarQualityAssuranceContract()
            contract.createCarQuality(ctx, "1003", 1)
            verify(stub, times(1)).putState("1003", "{\"value\":1}".toByteArray(Charsets.UTF_8))
        }

        @Test
        fun `should throw an error for a car quality that already exists`() {
            val contract = CarQualityAssuranceContract()
            val e = assertThrows(RuntimeException::class.java) { contract.createCarQuality(ctx, "1001", 2) }
            assertEquals(e.message, "Quality already exists for car 1001")
        }

        @Test
        fun `should throw an error for a car quality that is not a percentage value`() {
            val contract = CarQualityAssuranceContract()
            val e = assertThrows(RuntimeException::class.java) { contract.createCarQuality(ctx, "2137", 101) }
            assertEquals(e.message, "Quality value has to be a percentage value [1-100]")
        }

    }

    @Nested
    inner class readCarQuality {

        @Test
        fun `should return a car quality`() {
            val contract = CarQualityAssuranceContract()
            val carQuality = contract.readCarQuality(ctx, "1001")
            assertEquals(1, carQuality.value)
        }

        @Test
        fun `should throw an error for a car quality that does not exist`() {
            val contract = CarQualityAssuranceContract()
            val e = assertThrows(RuntimeException::class.java) { contract.readCarQuality(ctx, "1003") }
            assertEquals(e.message, "Quality already exists for car 1003")
        }

    }

    @Nested
    inner class updateCarQuality {

        @Test
        fun `should update a car quality`() {
            val contract = CarQualityAssuranceContract()
            contract.updateCarQuality(ctx, "1001", 3)
            verify(stub, times(1)).putState("1001", "{\"value\":3}".toByteArray(Charsets.UTF_8))
        }

        @Test
        fun `should throw an error for a car quality that does not exist`() {
            val contract = CarQualityAssuranceContract()
            val e = assertThrows(RuntimeException::class.java) { contract.updateCarQuality(ctx, "1003", 4) }
            assertEquals(e.message, "Quality already exists for car 1003")
        }

        @Test
        fun `should throw an error for a car quality that is not a percentage value`() {
            val contract = CarQualityAssuranceContract()
            val e = assertThrows(RuntimeException::class.java) { contract.updateCarQuality(ctx, "2137", 121) }
            assertEquals(e.message, "Quality value has to be a percentage value [1-100]")
        }

    }

    @Nested
    inner class deleteCarQuality {

        @Test
        fun `should delete a car quality`() {
            val contract = CarQualityAssuranceContract()
            contract.deleteCarQuality(ctx, "1001")
            verify(stub, times(1)).delState("1001")
        }

        @Test
        fun `should throw an error for a car quality that does not exist`() {
            val contract = CarQualityAssuranceContract()
            val e = assertThrows(RuntimeException::class.java) { contract.deleteCarQuality(ctx, "1003") }
            assertEquals(e.message, "Quality doesn't exist for car 1003")
        }

    }

}
