package org.needle4k.db

import org.needle4k.NeedleSession
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.InjectionTargetInformation
import java.io.PrintWriter
import java.sql.Connection
import java.util.logging.Logger
import javax.sql.DataSource

class DataSourceProvider : InjectionProvider<DataSource> {
  private lateinit var needleSession: NeedleSession
  private val dataSource: DataSource by lazy { JPADataSource(needleSession) }

  override fun getInjectedObject(injectionTargetType: Class<*>) = dataSource

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) = DataSource::class.java

  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>) =
    injectionTargetInformation.injectedObjectType === DataSource::class.java

  override fun initialize(needleSession: NeedleSession) {
    this.needleSession = needleSession
  }
}

class JPADataSource(private val needleSession: NeedleSession) : DataSource {
  private var logWriter: PrintWriter = PrintWriter(System.out)

  override fun getLogWriter() = logWriter

  override fun setLogWriter(out: PrintWriter) {
    this.logWriter = out
  }

  override fun setLoginTimeout(seconds: Int) {
  }

  override fun getLoginTimeout() = 0

  override fun getParentLogger(): Logger {
    throw UnsupportedOperationException()
  }

  override fun <T : Any> unwrap(iface: Class<T>): T {
    throw UnsupportedOperationException()
  }

  override fun isWrapperFor(iface: Class<*>) = false

  override fun getConnection(): Connection = getConnection("", "")

  override fun getConnection(username: String, password: String): Connection =
    needleSession.jpaInjectorConfiguration.hibernateSession.connection()
}
