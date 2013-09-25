# Jruby script to create HBase Table"

include Java
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.HConstants
import org.apache.hadoop.hbase.HTableDescriptor
import org.apache.hadoop.hbase.client.HBaseAdmin
import org.apache.hadoop.hbase.client.HTable


conf = HBaseConfiguration.new
tablename = "WIKIDUMP"
tablename2 = "crunchExample"
desc = HTableDescriptor.new(tablename)
desc2 = HTableDescriptor.new(tablename2)
desc.addFamily(HColumnDescriptor.new("RAW"))
desc2.addFamily(HColumnDescriptor.new("RAW"))
admin = HBaseAdmin.new(conf)
admin.createTable(desc)
admin.createTable(desc2)

