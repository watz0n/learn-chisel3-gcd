//=======================================================================
// Chisel3 GCD Synthsizable Example
// Watson Huang
// Nov 22, 2017
// GCD Module, combine cpath and dpath
//=======================================================================
package synth

import chisel3._

class gcd_synth extends Module {
  val io = IO(new Bundle {
    val operands_bits_A  = Input(UInt(16.W))
    val operands_bits_B  = Input(UInt(16.W))
    val operands_val = Input(UInt(1.W))
    val operands_rdy = Output(UInt(1.W))

    val result_bits_data = Output(UInt(16.W))
    val result_val = Output(UInt(1.W))
    val result_rdy = Input(UInt(1.W))
  })

  val cpath = Module(new gcd_cpath())
  val dpath = Module(new gcd_dpath())

  io.operands_rdy := cpath.io.operands_rdy
  io.result_val := cpath.io.result_val

  cpath.io.operands_val := io.operands_val
  cpath.io.result_rdy := io.result_rdy
  
  dpath.io.operands_bits_A := io.operands_bits_A
  dpath.io.operands_bits_B := io.operands_bits_B

  io.result_bits_data := dpath.io.result_bits_data

  cpath.io.B_zero := dpath.io.B_zero
  cpath.io.A_lt_B := dpath.io.A_lt_B

  dpath.io.A_en := cpath.io.A_en
  dpath.io.A_sel := cpath.io.A_sel
  dpath.io.B_en := cpath.io.B_en
  dpath.io.B_sel := cpath.io.B_sel
  
}
