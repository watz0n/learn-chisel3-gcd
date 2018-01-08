//=======================================================================
// Chisel3 GCD Synthsizable Example
// Watson Huang
// Nov 22, 2017
// GCD Control Path
//=======================================================================
package synth

import chisel3._
import chisel3.util._
import synth.consts._

class gcd_cpath extends Module with gcd_consts {
  val io = IO(new Bundle {
    
    //External Control Input
    val operands_val = Input(UInt(1.W))
    val result_rdy = Input(UInt(1.W))

    //External Control Output
    val operands_rdy = Output(UInt(1.W))
    val result_val = Output(UInt(1.W))
    
    //C2D Input
    val B_zero = Input(UInt(1.W))
    val A_lt_B = Input(UInt(1.W))

    //D2C Output
    val A_en = Output(UInt(1.W))
    val A_sel = Output(UInt(2.W))
    val B_en = Output(UInt(1.W))
    val B_sel = Output(UInt(1.W))

  })
    
    //State enumration
    val sWAIT :: sCALC :: sDONE :: Nil = Enum(3)
    //State register
    val state = Reg(UInt(2.W))

    //Output Register
    val op_rdy = Reg(UInt(1.W))
    val re_val = Reg(UInt(1.W))
    
    io.operands_rdy := op_rdy
    io.result_val := re_val

    io.A_en := UInt(0, 1)
    io.A_sel := A_MUX_SEL_X
    io.B_en := UInt(0, 1)
    io.B_sel := B_MUX_SEL_X

    op_rdy := UInt(0, 1)
    re_val := UInt(0, 1)

    //Control Signal Output
    switch(state) {
        is(sWAIT) {
            io.A_en := UInt(1, 1)
            io.A_sel := A_MUX_SEL_IN
            io.B_en := UInt(1, 1)
            io.B_sel := B_MUX_SEL_IN
            op_rdy := UInt(1, 1)
        }
        is(sCALC) {
            when(io.A_lt_B === UInt(1,1)) {
                io.A_en := UInt(1, 1)
                io.A_sel := A_MUX_SEL_B
                io.B_en := UInt(1, 1)
                io.B_sel := B_MUX_SEL_A
            }
            .elsewhen(io.B_zero === UInt(0,1)) {
                io.A_en := UInt(1, 1)
                io.A_sel := A_MUX_SEL_SUB
            }
        }
        is(sDONE) {
            re_val := UInt(1, 1)
        }
    }

    //State upadte
    switch(state) {
        is(sWAIT) {
            when(io.operands_val === UInt(1,1)) {
                state := sCALC
            }
        }
        is(sCALC) {
            when(io.B_zero === UInt(1,1)) {
                state := sDONE
            }
        }
        is(sDONE) {
            when(io.result_rdy === UInt(1,1)) {
                state := sWAIT
            }
        }
    }

    when(reset.toBool()) {
        state := sWAIT
    }

}