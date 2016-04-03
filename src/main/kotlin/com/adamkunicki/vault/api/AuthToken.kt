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

package com.adamkunicki.vault.api

import com.adamkunicki.vault.VaultConfiguration
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.github.salomonbrys.kotson.jsonObject

class AuthToken(private val conf: VaultConfiguration) {
  val UTF_8 = Charsets.UTF_8.name()

  fun create(options: List<Pair<String, Any?>>): Secret {
    val (request, response, result) = (conf.adddress + "/v1/auth/token/create")
        .httpPost()
        .body(jsonObject(*options.toTypedArray()).toString(), Charsets.UTF_8)
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return result
  }

  fun renew(id: String, increment: Int = 0): Secret {
    val (request, response, result) = (conf.adddress + "/v1/auth/token/renew/" + id)
        .httpPut()
        .body(jsonObject("increment" to increment).toString(), Charsets.UTF_8)
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return result
  }

  fun renewSelf(increment: Int = 0): Secret {
    val (request, response, result) = (conf.adddress + "/v1/auth/token/renew-self")
        .httpPut()
        .body(jsonObject("increment" to increment).toString(), Charsets.UTF_8)
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return result
  }

  fun revokeSelf(): Int {
    val (request, response, result) = (conf.adddress + "/v1/auth/token/revoke-self")
        .httpPost()
        .header(Pair("X-Vault-Token", conf.token))
        .response()

    return response.httpStatusCode
  }

  fun revokeOrphan(id: String): Boolean {
    val (request, response, result) = (conf.adddress + "/v1/auth/token/revoke-orphan/" + id)
        .httpPut()
        .header(Pair("X-Vault-Token", conf.token))
        .responseObject(Secret.Deserializer())

    return true
  }

  fun revokePrefix(prefix: String): Boolean {
    val (request, response, result) = (conf.adddress + "/v1/auth/token/revoke-prefix/" + prefix)
        .httpPut()
        .header(Pair("X-Vault-Token", conf.token))
        .response()

    return true
  }

  fun revokeTree(id: String): Boolean {
    val (request, response, result) = (conf.adddress + "/v1/auth/token/revoke/" + id)
        .httpPut()
        .header(Pair("X-Vault-Token", conf.token))
        .response()

    return true
  }
}