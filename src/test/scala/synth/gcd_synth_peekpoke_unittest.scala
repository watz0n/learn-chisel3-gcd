//=======================================================================
// Chisel3 GCD Synthsizable Unit-Test
// Watson Huang
// Nov 27, 2017
// Peek Poke Tester Example, good step and peek function
//=======================================================================
package synth

import chisel3._
import chisel3.iotesters._
import org.scalatest._              // For matchers class to enable "should be (true)" statement
import org.scalatest.exceptions._   // For the "TestFailedException" declaration //Ref: https://www.programcreek.com/scala/org.scalatest.exceptions.TestFailedException

class GCDSynthPeekPokeTester(dut: gcd_synth, a:Int, b:Int, c:Int) extends PeekPokeTester(dut)  {

  var count = 0

  poke(dut.io.operands_bits_A, a)
  poke(dut.io.operands_bits_B, b)
  poke(dut.io.operands_val, 0)
  poke(dut.io.result_rdy, 1)

  step(1)
  count += 1
  poke(dut.io.operands_val, 1)
  step(1)
  count += 1

  while(peek(dut.io.result_val) == BigInt(0)) {
      step(1)
      count += 1;
  }

  expect(dut.io.result_bits_data, c)
  step(1)

}

class GCDSynthPeekPokeSpec extends ChiselFlatSpec with Matchers {
  
  //Use scalatest FlatSpec style, build-in ChiselFlatSpec
  //Ref: http://www.scalatest.org/at_a_glance/FlatSpec

  it should "Test1: gcd_synth should be elaborate normally" in {
    elaborate { 
      new gcd_synth
    }
    info("elaborate gcd_synth done")
  }

  it should "Test2: GCDSynth Tester return the correct result" in {
    
    //Reference for TesterOptionsManager
    //Ref: https://github.com/freechipsproject/chisel-testers/blob/master/src/test/scala/examples/GCDSpec.scala
    //Source: https://github.com/freechipsproject/chisel-testers/blob/master/src/main/scala/chisel3/iotesters/TesterOptions.scala
    val manager = new TesterOptionsManager {
      //testerOptions = testerOptions.copy(backendName = "firrtl")
      //interpreterOptions = interpreterOptions.copy(writeVCD = true)
      testerOptions = testerOptions.copy(backendName = "verilator")
    }

    var test_count = 0
    val gcd_tests = List(
    (27, 15, 3), //No. 1 test case
    (21, 49, 7),
    (25, 30, 5),
    (19, 27, 1),
    (40, 40, 40),
    (250, 190, 10),
    (5, 250, 5),
    (0, 0, 0))

    gcd_tests.foreach { tupleElement => {
      val (a:Int, b:Int, c:Int) = tupleElement
      test_count += 1
      //Use TestFailedException to print error state
      //Ref: http://www.scalatest.org/user_guide/using_matchers
      try {
        chisel3.iotesters.Driver.execute(() => new gcd_synth, manager) {
          dut => new GCDSynthPeekPokeTester(dut, a, b, c)
        } should be (true)
      } catch {
        case tfe: TestFailedException => {
          info("Failed on No.%d tests".format(test_count))
          info("a:%d, b:%d, c:%d".format(a, b, c))
          throw tfe
        }
      }
    }}

    info("Passed all %d tests".format(test_count))
  }
}
