package org.tedtenedorio.glide.storage.cache

import com.github.benmanes.caffeine.cache.LoadingCache
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes

object GlobalCaches {
  val createdAtCache: LoadingCache<String, Long> = CacheOptions<String, Long>(
    gcAware = true
  ) asLoadingCache { path ->
    Files
      .readAttributes(Paths.get(path), BasicFileAttributes::class.java)
      .creationTime()
      .toMillis()
  }
  val updatedAtCache: LoadingCache<String, Long> = CacheOptions<String, Long>(
    gcAware = true
  ) asLoadingCache { path ->
    Files
      .readAttributes(Paths.get(path), BasicFileAttributes::class.java)
      .lastModifiedTime()
      .toMillis()
  }
  val accessedAtCache: LoadingCache<String, Long> = CacheOptions<String, Long>(
    gcAware = true
  ) asLoadingCache { path ->
    Files
      .readAttributes(Paths.get(path), BasicFileAttributes::class.java)
      .lastAccessTime()
      .toMillis()
  }
}