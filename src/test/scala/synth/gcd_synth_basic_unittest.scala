//=======================================================================
// Chisel3 GCD Synthsizable Unit-Test
// Watson Huang
// Nov 22, 2017
// Basic Tester Example
//=======================================================================
package synth

import chisel3._
import chisel3.testers.BasicTester
import chisel3.iotesters.ChiselPropSpec

class GCDSynthBasicTester(a: Int, b: Int, z: Int) extends BasicTester {

    val dut = Module(new gcd_synth)

    val reg_op_val = Reg(UInt(1.W))
    val reg_re_rdy = Reg(UInt(1.W))
    val reg_inA = Reg(UInt(16.W))
    val reg_inB = Reg(UInt(16.W))

    val expect_result = UInt(z, 16)

    dut.io.operands_bits_A := reg_inA
    dut.io.operands_bits_B := reg_inB
    dut.io.operands_val := reg_op_val
    dut.io.result_rdy := reg_re_rdy
    
    when(reg_op_val === UInt(0, 1)) {reg_op_val := UInt(1, 1)}   
    when(dut.io.result_val === UInt(1, 1)) {
        reg_re_rdy := UInt(1, 1)
        assert(dut.io.result_bits_data === expect_result)
        stop()
    }

    when(reset.toBool()) {
        reg_op_val := UInt(0, 1)
        reg_re_rdy := UInt(0, 1)
        reg_inA := UInt(a, 16)
        reg_inB := UInt(b, 16)
    }
}

class GCDSynthBasicSpec extends ChiselPropSpec {

  //Build PropSpec form Chisel3 example
  //Ref: https://github.com/freechipsproject/chisel3/blob/master/src/test/scala/chiselTests/GCD.scala

  property("Test1: gcd_synth should be elaborate normally") {
    elaborate { new gcd_synth }
    info("Elaborate gcd_synth done")
  }

  property("Test2: GCDSynth Tester return the correct result") {
    
    var test_count = 0
    val gcd_tests = Table(
    ("a", "b", "z"),  // First tuple defines column names
    (27, 15, 3),  // Subsequent tuples define the data //No. 1 test case
    (21, 49, 7),
    (25, 30, 5),
    (19, 27, 1),
    (40, 40, 40),
    (250, 190, 10),
    (5, 250, 5),
    (0, 0, 0))

    forAll (gcd_tests) { (a: Int, b: Int, z: Int) =>
      test_count += 1
      assertTesterPasses{ new GCDSynthBasicTester(a, b, z) }
    }

    info("Passed all %d tests".format(test_count))
  }

}
