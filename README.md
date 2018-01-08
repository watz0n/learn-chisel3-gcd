Learning Chisel3 by building GCD Module
===

This is a simple project to learn by doing from scratch, aim to understand how to build synthesizable circuit from Chisel3.

When learning digital design, it's crucial to distinguish the different between behavioral model and synthesizable design. The definitive difference is synthesizable design could generate gate-level (AND/OR/XOR Gates) efficiency, but behavioral model is less efficiency or can't generate gate-level model because it would violate desige-rule in Physics.

Therefore, I survey most US university VLSI laboratory, chose Greatest Common Divisor (GCD) module as entry point. The behavior model from [UC Riverside EECS168](https://github.com/sheldonucr/ucr-eecs168-lab/tree/master/lab4), the synthesizable design form [MIT 6.884 GCD Lecture](http://csg.csail.mit.edu/6.884/handouts/lectures/L02-Verilog.pdf), and the common interface form [UC Berkeley CS250 FA09 LAB1](https://inst.eecs.berkeley.edu/~cs250/fa09/handouts/lab1-gcd.pdf).

This project is modified form [chisel-template project](https://github.com/ucb-bar/chisel-template), but fix chisel3 version to old `3.0-SNAPSHOT` in `build.sbt` for behavioral model simulation. Because the newer `3.0.+` leads to large un-initialized error in Verilator.

Adhere, we are talking about how to building GCD module in this repo. If you are interesting how I choose these material and implementation in detail, please reference [my development notes]().

Setup Chisel3 Build Environment
===

We need two software to use Chisel3: `sbt` and `Verilator 3.906`.
The official project has comprehensive installation guide, or you can reference my progress. By the way, My build system is Ubuntu 16.04 under Windows10 via Bash on Windows, if you like setup same environment, please reference [my old win10 setup-up process](https://github.com/wats0n/install-chisel-win10).

Follow the official Chisel3 installation guide
---
Following the Linux Installation Guide from [Chisel3 project page](https://github.com/freechipsproject/chisel3), 
and it's better to update Verilator to v3.906 from [Sodor project page](https://github.com/librecores/riscv-sodor#building-the-processor-emulators), my environment chosen installation from .tgz package.

My Chisel3 installation progress
---
Here is a simplified progress to setup `sbt` environment:
```bash
#install sbt form chisel3 projcet
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
sudo apt-get update
#Install necessary packages for verilator and chisel3
sudo apt-get install git make autoconf g++ flex bison default-jdk sbt
```
Next step is Verilator installation progress. I usually install Verilator under `~/work/verilator` directory:
```bash
cd ~/
mkdir work
cd work
# reference form riscv-sodor
wget https://www.veripool.org/ftp/verilator-3.906.tgz
tar -xzf verilator-3.906.tgz
mv verilator-3.906 verilator
cd verilator
unset VERILATOR_ROOT
./configure
make
export VERILATOR_ROOT=$PWD
export PATH=$PATH:$VERILATOR_ROOT/bin
```

Get the repo.
===
```bash
git clone https://github.com/watz0n/chisel3-gcd.git
cd chisel3-gcd
```

Directory structue in repo.
===

* .\project\ : Project settings and compiled class directory
* .\src\main\scala\ : Chisel3 circuit codes
    * .\src\main\scala\behav : Behavioral model
    * .\src\main\scala\synth : Synthesizable design
    * .\src\main\scals\synthio : Synthesizable design with Bulk IO connection
* .\src\test\scala\ : Chisel3 test-bench codes
    * .\src\main\test\behav : Behavioral test-bench
    * .\src\main\test\synth : Synthesizable test-bench
    * .\src\main\test\synthio : Synthesizable IO test-bench

Use Chisel3 Unit-Test Function
===

This project would implementation two Chisel3 test-bench, `BasicTester` and `PeekPokeTester`. The `BasicTester` is coming from chisel-template/chisel-tutorial project, but the `PeekPokeTester` has better features like step function, peek current input/output status, poke new data into input, much like Verilog test-bench.

Use `sbt` to perform Unit-Test
---
The `sbt` command has convi [testOnly function](https://stackoverflow.com/questions/11159953/scalatest-in-sbt-is-there-a-way-to-run-a-single-test-without-tags). 
Not `test-only`, which I can't make it work.
```bash
# Unit-Test for GCD Behavioral BasicTester
sbt "testOnly *behav.GCDBehavBasicSpec"
# Unit-Test for GCD Behavioral PeekPokeTester
sbt "testOnly *behav.GCDBehavPeekPokeSpec"
# Unit-Test for GCD Synthesizable BasicTester
sbt "testOnly *synth.GCDSynthBasicSpec"
# Unit-Test for GCD Synthesizable PeekPokeTester
sbt "testOnly *synth.GCDSynthPeekPokeSpec"
# Unit-Test for GCD Synthesizable with Bulk IO BasicTester
sbt "testOnly *synthio.GCDSynthIOBasicSpec"
# Unit-Test for GCD Synthesizable with Bulk IO PeekPokeTester
sbt "testOnly *synthio.GCDSynthIOPeekPokeSpec"
```
The `*` in `sbt "testOnly *behav.GCDBehavBasicSpec"` means we want to test the `GCDBehavBasicSpec` class in `behav` package form ANY directory.

Use pre-defined script to perform Unit-Test
---
```bash
# Unit-Test for GCD Behavioral BasicTester
bash unit-test-btr-behav.sh
# Unit-Test for GCD Behavioral PeekPokeTester
bash unit-test-pptr-behav.sh
# Unit-Test for GCD Synthesizable BasicTester
bash unit-test-btr-synth.sh
# Unit-Test for GCD Synthesizable PeekPokeTester
bash unit-test-pptr-synth.sh
# Unit-Test for GCD Synthesizable with Bulk IO BasicTester
bash unit-test-btr-synthio.sh
# Unit-Test for GCD Synthesizable with Bulk IO PeekPokeTester
bash unit-test-pptr-synthio.sh
```

Clean up function
---
Because we use Chisel3 test function would generate large meta-data like `./test_run_dir`, I've write a script to clean it:
```bash
bash unit-clear.sh
```
There is a more strong cleaner, not only clean meta-data, but also clean compiled Chisel3 class data:
```bash
bash clear-deep.sh
```

Learning Material
===

University Courses (Online Data)
---
* [UC Riverside EECS168](https://github.com/sheldonucr/ucr-eecs168-lab/tree/master/lab4) : Use this simple GCD behavioral model.
* [MIT 6.884](http://csg.csail.mit.edu/6.884/handouts.html) : The original GCD module design data, use the synthesizable design (cpath/dpath) as reference.
* [Berkeley CS250 FA09](https://inst.eecs.berkeley.edu/~cs250/fa09/) : Universal GCD module input/output interface for implementation. 

Books
---
* [Programming in Scala, First Edition](https://www.artima.com/pins1ed/) : Comprehensive Scala introduction book, strong recommendation to read before implementation.

Wikis
---
* [Chisel3 Official Wiki](https://github.com/freechipsproject/chisel3/wiki) : 
Lots of Chisel3 use cases and examples.

FAQs
===
*Hey! You have some typo or something wrong! Where are you?*
* Directly use issue or pull request
* E-Mail: watz0n.tw@gmail.com
* Website: TBD