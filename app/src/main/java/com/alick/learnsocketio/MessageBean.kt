package com.example

import java.io.Serializable

/**
 * @author 崔兴旺
 * @description
 * @date 2022/7/4 2:05
 */
data class MessageBean(val name: String = "client", val message: String) : Serializable