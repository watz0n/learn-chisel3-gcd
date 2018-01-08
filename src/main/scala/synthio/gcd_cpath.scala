//=======================================================================
// Chisel3 GCD Synthsizable with bulk IO connection Example
// Watson Huang
// Nov 23, 2017
// Control Path
//=======================================================================
package synthio

import chisel3._
import chisel3.util._
import synthio.consts._

class gcd_cpath extends Module with gcd_consts {
  val io = IO(new Bundle {
    
    //External Control Input
    val operands_val = Input(UInt(1.W))
    val result_rdy = Input(UInt(1.W))

    //External Control Output
    val operands_rdy = Output(UInt(1.W))
    val result_val = Output(UInt(1.W))
    
    //Group IO Declaration
    val gc = new gcd_ctrlio //Gcd Control

  })
    
    val B_zero = Wire(UInt(1.W))
    val A_lt_B = Wire(UInt(1.W))
    val A_en = Wire(UInt(1.W))
    val A_sel = Wire(UInt(2.W))
    val B_en = Wire(UInt(1.W))
    val B_sel = Wire(UInt(1.W))

    //Wire to Ouptut
    io.gc.A_en := A_en
    io.gc.A_sel := A_sel
    io.gc.B_en := B_en
    io.gc.B_sel := B_sel
    //Input to Wire
    B_zero := io.gc.B_zero
    A_lt_B := io.gc.A_lt_B

    //State enumration
    val sWAIT :: sCALC :: sDONE :: Nil = Enum(3)
    //State register
    val state = Reg(UInt(2.W))

    //Output Register
    val op_rdy = Reg(UInt(1.W))
    val re_val = Reg(UInt(1.W))
    
    io.operands_rdy := op_rdy
    io.result_val := re_val
    
    A_en := UInt(0, 1)
    A_sel := A_MUX_SEL_X
    B_en := UInt(0, 1)
    B_sel := B_MUX_SEL_X

    op_rdy := UInt(0, 1)
    re_val := UInt(0, 1)

    //Control Signal Output
    switch(state) {
        is(sWAIT) {
            A_en := UInt(1, 1)
            A_sel := A_MUX_SEL_IN
            B_en := UInt(1, 1)
            B_sel := B_MUX_SEL_IN
            op_rdy := UInt(1, 1)
        }
        is(sCALC) {
            when(A_lt_B === UInt(1,1)) {
                A_en := UInt(1, 1)
                A_sel := A_MUX_SEL_B
                B_en := UInt(1, 1)
                B_sel := B_MUX_SEL_A
            }
            .elsewhen(B_zero === UInt(0,1)) {
                A_en := UInt(1, 1)
                A_sel := A_MUX_SEL_SUB
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
            when(B_zero === UInt(1,1)) {
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