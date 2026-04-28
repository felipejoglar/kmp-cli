package kmp.cli

import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import kmp.cli.command.NewCommand
import kmp.cli.command.UpdateCommand

fun main(args: Array<String>) = KmpCli().subcommands(NewCommand(), UpdateCommand()).main(args)
