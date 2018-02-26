@file:Suppress("NOTHING_TO_INLINE")

package timber.log.pirage

import timber.log.Timber

inline fun verbose(msg: () -> String) = Timber.v(msg.toStringSafe())

inline fun debug(msg: () -> String) = Timber.d(msg.toStringSafe())
inline fun info(msg: () -> String) = Timber.i(msg.toStringSafe())
inline fun warn(msg: () -> String) = Timber.w(msg.toStringSafe())
inline fun err(msg: () -> String) = Timber.e(msg.toStringSafe())
inline fun err(e: Throwable, msg: () -> String) = Timber.e(e, msg.toStringSafe())

inline fun verbose(msg: String) = Timber.v(msg)
inline fun debug(msg: String) = Timber.d(msg)
inline fun info(msg: String) = Timber.i(msg)
inline fun warn(msg: String) = Timber.w(msg)
inline fun err(msg: String) = Timber.e(msg)
inline fun err(e: Throwable, msg: String) = Timber.e(e, msg)
inline fun err(e: Throwable) = Timber.e(e)


inline fun (() -> Any?).toStringSafe() = try {
    invoke().toString()
} catch (e: Exception) {
    "Log message invocation failed: $e"
}