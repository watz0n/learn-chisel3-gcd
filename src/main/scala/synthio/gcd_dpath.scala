//=======================================================================
// Chisel3 GCD Synthsizable with bulk IO connection Example
// Watson Huang
// Nov 23, 2017
// Data Path
//=======================================================================
package synthio

import chisel3._
import chisel3.util._
import synthio.consts._

class gcd_dpath extends Module with gcd_consts {
  val io = IO(new Bundle {
    
    //External Data Input
    val operands_bits_A  = Input(UInt(16.W))
    val operands_bits_B  = Input(UInt(16.W))

    //External Data Output
    val result_bits_data = Output(UInt(16.W))

    //Group IO Declaration, Reverse Direction
    val fgc = Flipped(new gcd_ctrlio) //Flpped Gcd Control

  })

    val B_zero = Wire(UInt(1.W))
    val A_lt_B = Wire(UInt(1.W))
    val A_en = Wire(UInt(1.W))
    val A_sel = Wire(UInt(2.W))
    val B_en = Wire(UInt(1.W))
    val B_sel = Wire(UInt(1.W))

    //Wire to Ouptut
    io.fgc.B_zero := B_zero
    io.fgc.A_lt_B := A_lt_B
    //Input to Wire
    A_en := io.fgc.A_en
    A_sel := io.fgc.A_sel
    B_en := io.fgc.B_en
    B_sel := io.fgc.B_sel

    val reg_a = Reg(UInt(16.W))
    val reg_b = Reg(UInt(16.W))
    val a_sub_b = reg_a - reg_b

    io.result_bits_data := reg_a
    B_zero := (reg_b === 0.U)
    A_lt_B := (reg_a < reg_b)

    when(A_en === UInt(1,1)) {
        reg_a := reg_a //Default Value for switch
        switch(A_sel) {
            is(A_MUX_SEL_IN) { reg_a := io.operands_bits_A }
            is(A_MUX_SEL_B) { reg_a := reg_b }
            is(A_MUX_SEL_SUB) { reg_a := a_sub_b}
        }
    }

    when(B_en === UInt(1,1)) {
        reg_b := reg_b //Default Value for switch
        switch(B_sel) {
            is(B_MUX_SEL_IN) { reg_b := io.operands_bits_B }
            is(B_MUX_SEL_A) { reg_b := reg_a }
        }
    }

    when(reset.toBool()) {
        reg_a := 0.U
        reg_b := 0.U
    }
}