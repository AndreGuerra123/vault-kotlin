/*
 * Copyright 2016 Adam Kunicki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adamkunicki.vault.api.sys

import com.adamkunicki.vault.VaultConfiguration
import com.adamkunicki.vault.VaultError
import com.adamkunicki.vault.VaultException
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPut
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.Gson
import java.io.Reader

@Suppress("UNUSED_VARIABLE")
class Audit(private val conf: VaultConfiguration) {

  class Deserializer : ResponseDeserializable<Map<String, AuditMount>> {
    override fun deserialize(reader: Reader): Map<String, AuditMount> = Gson().fromJson<Map<String, AuditMount>>(reader)
  }

  @Throws(VaultException::class)
  fun list(): Map<String, AuditMount> {
    val (request, response, result) = (conf.adddress + "/v1/sys/audit")
        .httpGet()
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Deserializer())

    val (secret, error) = result

    if (secret != null) {
      return secret
    }
    val errorMessage = if (error != null) {
      Gson().fromJson(String(error.errorData), VaultError::class.java).errors.joinToString()
    } else {
      ""
    }
    throw VaultException(errorMessage)
  }

  fun enable(
      path: String,
      type: String,
      description: String = "",
      options: List<Pair<String, Any?>> = emptyList()
  ): Boolean {
    val (request, response, result) = (conf.adddress + "/v1/sys/audit/" + path.trim('/'))
        .httpPut()
        .body(
            jsonObject(
                "type" to type,
                "description" to description,
                "options" to jsonObject(*options.toTypedArray())
            ).toString(),
            Charsets.UTF_8
        )
        .header(Pair("X-Vault-Token", conf.token))
        .response()

    return true
  }

  fun disable(path: String): Boolean {
    val (request, response, result) = (conf.adddress + "/v1/sys/audit/" + path.trim('/'))
        .httpDelete()
        .header(Pair("X-Vault-Token", conf.token))
        .response()

    return true
  }
}