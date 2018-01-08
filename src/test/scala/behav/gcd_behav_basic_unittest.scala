//=======================================================================
// Chisel3 GCD Behavior Unit-Test
// Watson Huang
// Nov 21, 2017
// Basic Tester Example
//=======================================================================
package behav

import chisel3._
import chisel3.testers.BasicTester
import chisel3.iotesters.ChiselPropSpec

class GCDBehavBasicTester(a: Int, b: Int, z: Int) extends BasicTester {
    val dut = Module(new gcd_behav)

    val reg_op_val = Reg(Bool())
    val reg_re_rdy = Reg(Bool())
    val reg_inA = Reg(UInt(16.W))
    val reg_inB = Reg(UInt(16.W))
    val expect_result = UInt(z, 16)

    dut.io.operands_bits_A := reg_inA
    dut.io.operands_bits_B := reg_inB
    dut.io.operands_val := reg_op_val
    dut.io.result_rdy := reg_re_rdy

    when(!reg_op_val) {reg_op_val := true.B}   
    when(dut.io.result_val) {
        reg_re_rdy := true.B
        assert(dut.io.result_bits_data === expect_result)
        stop()
    }

    when(reset.toBool()) {
        reg_inA := UInt(a, 16)
        reg_inB := UInt(b, 16)
        reg_op_val := false.B
        reg_re_rdy := false.B
    }
}

class GCDBehavBasicSpec extends ChiselPropSpec {
  
  //Build PropSpec form Chisel3 example
  //Ref: https://github.com/freechipsproject/chisel3/blob/master/src/test/scala/chiselTests/GCD.scala

  property("Test1: gcd_behav should be elaborate normally") {
    elaborate { new gcd_behav }
    info("Elaborate gcd_behav done")
  }

  property("Test2: GCDBehav Tester return the correct result") {
    
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
      assertTesterPasses{ new GCDBehavBasicTester(a, b, z) }
    }

    info("Passed all %d tests".format(test_count))
  }

}
