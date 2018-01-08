//=======================================================================
// Chisel3 GCD Behavior Example
// Watson Huang
// Nov 20, 2017
//=======================================================================
package behav

import chisel3._

class gcd_behav extends Module {
  val io = IO(new Bundle {
    val operands_bits_A  = Input(UInt(16.W))
    val operands_bits_B  = Input(UInt(16.W))
    val operands_val = Input(Bool())
    val operands_rdy = Output(Bool())

    val result_bits_data = Output(UInt(16.W))
    val result_val = Output(Bool())
    val result_rdy = Input(Bool())
  })

  val a = Reg(UInt(16.W))
  val b = Reg(UInt(16.W))
  val op_rdy = Reg(Bool())
  val re_val = Reg(Bool())

  val do_gcdcalc = (~(op_rdy|re_val))

  io.operands_rdy := op_rdy
  io.result_val := re_val

  when(do_gcdcalc) {
    when(b != 0.U) {
      when(a < b) {
        a := b
        b := a
      }
      .otherwise {
        a := a-b
      }
    }
    .otherwise {
      re_val := true.B
    }
  }

  when(op_rdy&io.operands_val) {
    a := io.operands_bits_A
    b := io.operands_bits_B
    io.result_bits_data := a
    op_rdy := false.B
  }

  when(reset.toBool()|(re_val&io.result_rdy)) {
    op_rdy := true.B
    re_val := false.B
  }
  
}
