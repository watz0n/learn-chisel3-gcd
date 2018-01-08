//=======================================================================
// Chisel3 GCD Synthsizable Example
// Watson Huang
// Nov 22, 2017
// Test declare constants from other package
//=======================================================================
package synth
package consts
{
    import chisel3._

    trait gcd_consts {

        val A_MUX_SEL_X     = UInt(0, 2)
        val A_MUX_SEL_IN    = UInt(0, 2)
        val A_MUX_SEL_B     = UInt(1, 2)
        val A_MUX_SEL_SUB   = UInt(2, 2)

        val B_MUX_SEL_X     = UInt(0, 2)
        val B_MUX_SEL_IN    = UInt(0, 2)
        val B_MUX_SEL_A     = UInt(1, 2)
    }
}