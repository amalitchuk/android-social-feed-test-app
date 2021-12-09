package com.socommtech.getsocial

import com.jraska.console.Console

fun String.consolePrint() {
    Console.writeLine("==========")
    Console.writeLine(this)
}

fun Any.consolePrint(){
    this.toString().consolePrint()
}