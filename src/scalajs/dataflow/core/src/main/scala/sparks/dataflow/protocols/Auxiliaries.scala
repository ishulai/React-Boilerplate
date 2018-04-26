package sparks.dataflow.protocols

import io.circe._
import io.circe.generic.semiauto._
import org.apache.spark.sql.types._

object Auxiliaries {

  object EntityType extends Enumeration {

    type EntityType = Value

    val Table, View, SystemTable, GlobalTemporary, LocalTemporary, Alias, Synonym = Value

    def parse(value: String) = value.toLowerCase() match {
      case "table" => Table
      case "view" => View
      case "systemtable" => SystemTable
      case "globaltemporary" => GlobalTemporary
      case "localtemporary" => LocalTemporary
      case "alias" => Alias
      case "synonym" => Synonym
    }
  }

  import EntityType._

  case class DbEntity(catalog: String, schema: String, name: String, qualifiedName: String, `type`: EntityType)

  case class EntityPreview(timeCached: String, preview: String)

  case class Schema(fields: Seq[Field]) {

    def find(name: String) = fields.find(_.name == name).orNull
    def contains(name: String, dataType: String) = fields.exists(f => f.name == name && f.dataType == dataType)
    def findDuplicates(target: Schema) = fields.filter(f => target.contains(f.name, f.dataType))
    def selectedFields = fields.filter(_.selected)
    def selected = Schema(selectedFields)
    def finalSchema = Schema(selectedFields.map(_.transform))
    def allSelected = fields.forall(_.selected)
    def selectAll = Schema(fields.map(f => Field(f.name, f.dataType, f.alias, f.newType, f.nullable, f.left, true, f.aggfunc, f.userfunc)))
    def unselectAll = Schema(fields.map(f => Field(f.name, f.dataType, f.alias, f.newType, f.nullable, f.left, false, f.aggfunc, f.userfunc)))
    def clearAlias = Schema(fields.map(f => Field(f.name, f.dataType, "", f.newType, f.nullable, f.left, f.selected, f.aggfunc, f.userfunc)))

    /**
      * Joins two schemas by name
      *
      * @param schema the schema to join
      * @return joined schema
      */
    def join(schema: Schema) = Schema(fields.map(f => f.join(schema.find(f.name))))
    def ++(newFields: Seq[Field]) = Schema(fields ++ newFields)

  }

  /**
    * Spark SQL types
    * Byte
    * Short
    * Int
    * Long
    * java.math.BigDecimal
    * Float
    * Double
    * Array[Byte] - Binary
    * Boolean
    * java.sql.Date
    * java.sql.Timestamp
    * String
    */
  case class Field(
                    name: String,
                    dataType: String,
                    alias: String = "",
                    newType: String = "",
                    nullable: Boolean = true,
                    left: Boolean = true,
                    selected: Boolean = true,
                    aggfunc: String = "",
                    userfunc: String = ""
                  ) {

    def transform = Field(
      name = if (alias.nonEmpty) alias else name,
      dataType = if (newType.nonEmpty) newType else dataType,
      "",
      "",
      nullable
    )

    // todo: Temp
    def sparkType: org.apache.spark.sql.types.DataType =
      dataType.toLowerCase match {
        case "string" => StringType
        case "int" => IntegerType
        case "float" => FloatType
        case "double" => DoubleType
      }

    def toStructField = StructField(name, sparkType, nullable)

    def join(field: Field) = {

      implicit class StringExtension(a: String) {
        def |(b: String) = if (a.nonEmpty) a else if (b.nonEmpty) b else ""
      }

      if (field == null)
        this
      else
        Field(
          name | field.name,
          dataType | field.dataType,
          alias | field.alias,
          newType | field.newType,
          nullable || field.nullable,
          left || field.left,
          selected || field.selected,
          aggfunc | field.aggfunc,
          userfunc | field.userfunc
        )
    }

  }

  trait JsonSupport {
    implicit val entityTypeEncoder: Encoder[EntityType.Value] = Encoder.enumEncoder(EntityType)
    implicit val entityTypeDecoder: Decoder[EntityType.Value] = Decoder.enumDecoder(EntityType)
    implicit val dbEntityEncoder: Encoder[DbEntity] = deriveEncoder
    implicit val dbEntityDecoder: Decoder[DbEntity] = deriveDecoder
    implicit val fieldEncoder: Encoder[Field] = deriveEncoder
    implicit val fieldDecoder: Decoder[Field] = deriveDecoder
    implicit val schemaEncoder: Encoder[Schema] = deriveEncoder
    implicit val schemaDecoder: Decoder[Schema] = deriveDecoder
    implicit val entityPreviewEncoder: Encoder[EntityPreview] = deriveEncoder
  }

}