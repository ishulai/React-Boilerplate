package sparks.dataflow.connector.mssql

import sparks.dataflow.connector.ConnectionTester
import sparks.webui.entity.validators.{RangeValidator, RequiredValidator}
import sparks.webui.entity.{FieldSpec, FieldType}

case class MSSqlEditor(proxy: MSSqlProxy) extends ConnectionTester {

  val title = "Microsoft SQL Server"
  override val fixedUIWidth = 400

  override val fieldSpecs = Seq(
    FieldSpec("server", "Server", FieldType.Text, validator = RequiredValidator()),
    FieldSpec("adv", "Advanced settings", FieldType.Collapsable,
      childSpecs = Seq(
        FieldSpec("timeout", "Connection timeout", FieldType.Text, validator = RangeValidator(0, 9999)),
        FieldSpec("port", "Port", FieldType.Text, validator = RangeValidator(0, 65535)),
        FieldSpec("props", "Additional properties (optional)", FieldType.TextArea, height = 50)
      )
    ),
    FieldSpec("username", "User name", FieldType.Text, validator = RequiredValidator()),
    FieldSpec("password", "Password", FieldType.Password)
  )

  override def fill() = {
    form.setValue("server", proxy.hostName)
    form.setValue("timeout", proxy.timeout)
    form.setValue("port", proxy.port)
    form.setValue("props", proxy.properties)
    form.setValue("username", proxy.userName)
    form.setValue("password", proxy.password)
  }

  override def collect() = {
    proxy.hostName = form.valueOf("server")
    proxy.timeout = form.valueOf("timeout").toInt
    proxy.port = form.valueOf("port").toInt
    proxy.properties = form.valueOf("props")
    proxy.userName = form.valueOf("username")
    proxy.password = form.valueOf("password")
  }


}
