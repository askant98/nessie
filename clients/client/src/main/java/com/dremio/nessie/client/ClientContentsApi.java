/*
 * Copyright (C) 2020 Dremio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dremio.nessie.client;

import javax.validation.constraints.NotNull;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.dremio.nessie.api.ContentsApi;
import com.dremio.nessie.error.NessieConflictException;
import com.dremio.nessie.error.NessieNotFoundException;
import com.dremio.nessie.model.Contents;
import com.dremio.nessie.model.ContentsKey;
import com.dremio.nessie.model.MultiGetContentsRequest;
import com.dremio.nessie.model.MultiGetContentsResponse;

class ClientContentsApi implements ContentsApi {

  private final WebTarget target;

  public ClientContentsApi(WebTarget target) {
    this.target = target;
  }


  @Override
  public Contents getContents(@NotNull ContentsKey key, String ref) throws NessieNotFoundException {
    return target.path("contents").path(key.toPathString())
                 .queryParam("ref", ref)
                 .request()
                 .accept(MediaType.APPLICATION_JSON_TYPE)
                 .get()
                 .readEntity(Contents.class);
  }

  @Override
  public MultiGetContentsResponse getMultipleContents(@NotNull String ref, @NotNull MultiGetContentsRequest request)
      throws NessieNotFoundException {
    return target.path("contents")
        .queryParam("ref", ref)
        .request()
        .accept(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE))
        .readEntity(MultiGetContentsResponse.class);
  }


  @Override
  public void setContents(@NotNull ContentsKey key, String branch, @NotNull String hash, String message,
                          @NotNull Contents contents) throws NessieNotFoundException, NessieConflictException {
    target.path("contents").path(key.toPathString())
          .queryParam("branch", branch)
          .queryParam("hash", hash)
          .queryParam("message", message)
          .request()
          .post(Entity.entity(contents, MediaType.APPLICATION_JSON_TYPE));
  }

  @Override
  public void deleteContents(ContentsKey key, String branch, String hash, String message)
      throws NessieNotFoundException, NessieConflictException {
    target.path("contents").path(key.toPathString())
          .queryParam("branch", branch)
          .queryParam("hash", hash)
          .queryParam("message", message)
          .request()
          .delete();
  }
}
