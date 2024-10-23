package com.ivy.math

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.parser.Parser
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.math.exp

internal class ExpressionParserTest {
    private lateinit var parser: Parser<TreeNode>

    @BeforeEach
    fun setUp() {
        parser = expressionParser()
    }

    @ParameterizedTest
    @CsvSource(
        "(3*(18/3))/3, 6.0",
        "5+5, 10.0",
        "9*(3+6), 81.0",
        "9/(3+3), 1.5"
    )
    fun `Test evaluating expression`(expression: String, expected: Double) {
        val result = parser(expression).first()

        val actual = result.value.eval()

        assertThat(actual).isEqualTo(expected)
    }
}