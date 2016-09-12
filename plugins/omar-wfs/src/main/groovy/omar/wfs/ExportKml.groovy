package omar.wfs
import geoscript.filter.Expression
import groovy.xml.StreamingMarkupBuilder
import geoscript.layer.Layer
import groovy.xml.XmlUtil
import geoscript.feature.Feature
/**
 * Created by gpotts on 9/2/16.
 */
class ExportKml
{
   private static String getValue(Object template, Object obj) {
      if (template instanceof Closure) {
         (template as Closure).call(obj)
      } else if (template instanceof Expression) {
         (template as Expression).evaluate(obj)
      } else {
         template as String
      }
   }
   static String toKML(Layer layer, OutputStream out = System.out, Closure nameClosure = {f -> f.id}, Closure descriptionClosure = null)
   {
      def xml
      def markupBuilder = new StreamingMarkupBuilder()
      def featureWriter = new geoscript.feature.io.KmlWriter()
      xml = markupBuilder.bind { builder ->
         mkp.xmlDeclaration()
         mkp.declareNamespace([kml: "http://www.opengis.net/kml/2.2"])
         kml.kml {
            kml.Document {
               kml.Folder {
                  kml.name layer.name
                  kml.Schema ("kml:name": layer.name, "kml:id": layer.name) {
                     layer.schema.fields.each {fld ->
                        if(fld.name != "tie_point_set")
                        {
                           if (!fld.isGeometry()) {
                              kml.SimpleField("kml:name": fld.name, "kml:type": fld.typ)
                           }
                        }
                     }
                  }
                  layer.eachFeature {f ->
                     write( builder, f, [includeStyle:true, namespace: "kml", name: nameClosure, description: descriptionClosure])
                     //featureWriter.write builder, f,includeStyle:true, namespace: "kml", name: nameClosure, description: descriptionClosure
                  }
               }
            }
         }
      }

      XmlUtil.serialize(xml, out)
   }
   static String toKMLString(Layer layer, Closure nameClosure = {f -> f.id}, Closure descriptionClosure = null) {
      ByteArrayOutputStream out = new ByteArrayOutputStream()
      toKML(layer, out, nameClosure, descriptionClosure)
      out.toString()
   }
   static void write(def builder, Feature feature, Map options = [:]) {
      String namespace = options.get("namespace","")
      String ns = namespace.isEmpty() ? "" : "${namespace}:"
      def nameValue = options.get("name", {Feature f -> f.id})
      def descriptionValue = options.get("description")
      boolean extendedData = options.get("extendedData", true)
      boolean includeStyle = options.get("includeStyle", true)
      String color = geoscript.filter.Color.toHex(options.get("color","#ff0000ff")).replace("#","")
      String geometryType = feature.schema.geom.typ.toLowerCase()
      def geometryWriter = new geoscript.geom.io.KmlWriter()

      builder."${ns}Placemark" {
         builder."${ns}name" { mkp.yield(getValue(nameValue, feature)) }
         if (descriptionValue != null) {
            builder."${ns}description" { mkp.yield(getValue(descriptionValue, feature)) }
         }
         if (includeStyle) {
            builder."${ns}Style" {
               def geomType = geometryType.toLowerCase()
               if (geomType.endsWith("point")) {
                  builder."${ns}IconStyle" {
                     builder."${ns}color"("${color}")
                  }
               } else {
                  builder."${ns}LineStyle" {
                     builder."${ns}color"("${color}")
                  }
                  if (geomType.endsWith("polygon")) {
                     builder."${ns}PolyStyle" {
                        builder."${ns}fill"("0")
                     }
                  }
               }
            }
         }

         if (extendedData) {
            builder."${ns}ExtendedData" {
               builder."${ns}SchemaData" ("${ns}schemaUrl": "#${feature.schema.name}") {
                  feature.schema.fields.each {fld ->

                     if ((!fld.isGeometry())&&(fld.name!="tie_point_set")) {
                        builder."${ns}SimpleData" ("${ns}name": fld.name) { mkp.yield(feature.get(fld.name)) }
                     }
                  }
               }
            }
         }
         geometryWriter.write builder, feature.geom, namespace: namespace
      }
   }
}
