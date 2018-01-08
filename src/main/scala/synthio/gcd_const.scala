//=======================================================================
// Chisel3 GCD Synthsizable with bulk IO connection Example
// Watson Huang
// Nov 23, 2017
// Test declare constants from other package
//=======================================================================
package synthio
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

    class gcd_ctrlio extends Bundle { //GCD control IO
        //C2D Input
        val B_zero = Input(UInt(1.W))
        val A_lt_B = Input(UInt(1.W))

        //D2C Output
        val A_en = Output(UInt(1.W))
        val A_sel = Output(UInt(2.W))
        val B_en = Output(UInt(1.W))
        val B_sel = Output(UInt(1.W))
    }

}