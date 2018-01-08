//=======================================================================
// Chisel3 GCD Synthsizable Example
// Watson Huang
// Nov 22, 2017
// GCD Data Path
//=======================================================================
package synth

import chisel3._
import chisel3.util._
import synth.consts._

class gcd_dpath extends Module with gcd_consts {
  val io = IO(new Bundle {
    
    //External Data Input
    val operands_bits_A  = Input(UInt(16.W))
    val operands_bits_B  = Input(UInt(16.W))

    //External Data Output
    val result_bits_data = Output(UInt(16.W))

    //C2D Input
    val A_en = Input(UInt(1.W))
    val A_sel = Input(UInt(2.W))
    val B_en = Input(UInt(1.W))
    val B_sel = Input(UInt(1.W))
    
    //D2C Output
    val B_zero = Output(UInt(1.W))
    val A_lt_B = Output(UInt(1.W))

  })

    val reg_a = Reg(UInt(16.W))
    val reg_b = Reg(UInt(16.W))
    val a_sub_b = reg_a - reg_b

    io.result_bits_data := reg_a
    io.B_zero := (reg_b === 0.U)
    io.A_lt_B := (reg_a < reg_b)
    
    when(io.A_en === UInt(1,1)) {
        reg_a := reg_a //Default Value for switch
        switch(io.A_sel) {
            is(A_MUX_SEL_IN) { reg_a := io.operands_bits_A }
            is(A_MUX_SEL_B) { reg_a := reg_b }
            is(A_MUX_SEL_SUB) { reg_a := a_sub_b}
        }
    }

    when(io.B_en === UInt(1,1)) {
        reg_b := reg_b //Default Value for switch
        switch(io.B_sel) {
            is(B_MUX_SEL_IN) { reg_b := io.operands_bits_B }
            is(B_MUX_SEL_A) { reg_b := reg_a }
        }
    }

    when(reset.toBool()) {
        reg_a := 0.U
        reg_b := 0.U
    }
}