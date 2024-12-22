package com.machado001.lilol.rotation.model.local

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.machado001.lilol.Rotation.LocalRotation
import java.io.InputStream
import java.io.OutputStream

object RotationSerializer : Serializer<LocalRotation> {
  override val defaultValue: LocalRotation = LocalRotation.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): LocalRotation {
    try {
      return LocalRotation.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(
    t: LocalRotation,
    output: OutputStream
  ) = t.writeTo(output)
}
