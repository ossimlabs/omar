package omar.download
import groovy.io.FileType

import java.lang.reflect.Array
import java.util.zip.ZipOutputStream
import java.util.UUID
import java.util.ArrayList
import java.util.zip.ZipEntry


/**
 * Created by nroberts on 7/5/16.
 */

class ZipFile
{
   File file
   File pathName
   String baseName

   Integer aSize
   ArrayList fileList


    ZipFile(String f)
    {
        file = new File(f)
        pathName = new File(file?.parent)
        baseName = file?.name.toLowerCase()

    }

    ZipFile(ArrayList f)
    {
        fileList = f
    }


    void zip()
    {

        if(baseName == "a.toc")
        {
            pathName.eachFileRecurse (FileType.ANY) { dir ->

                def relPath = pathName.toPath().relativize( dir.toPath() ).toFile()
                println relPath

            }
        }
        else
        {

        }

    }

    void zipDir(OutputStream outputStream)
    {

        String zipFileID = UUID.randomUUID().toString();

        def zos = new ZipOutputStream( outputStream )
        byte[] readBuffer = new byte[2048];
        int bytesIn = 0


        def dirList = []

        pathName.eachDirRecurse() { dir ->

            def relPath = pathName.toPath().relativize( dir.toPath() ).toFile()
            String fPath = "${pathName.toString()}/${relPath.toString()}"

            //println dir.toPath()

            new File(fPath).eachFile() { dfile->
                if ( dfile.isDirectory() == false ) {
                    //println dfile.getName()

                    String dFileFullPath = "${fPath}/${dfile.getName()}"
                    String dFileRelPath = "${relPath.toString().minus(baseName)}/${dfile.getName()}"
                    //println dFileRelPath

                    FileInputStream fis = new FileInputStream( dFileFullPath );
                    ZipEntry anEntry = new ZipEntry( "${zipFileID}/${dFileRelPath}" );
                    zos.putNextEntry( anEntry );

                    while ( ( bytesIn = fis.read( readBuffer ) ) != -1 )
                    {
                        zos.write( readBuffer, 0, bytesIn );
                    }

                    fis.close();
                }
            }

        }
        zos.close();
    }

    void zip(OutputStream outputStream)
    {
        File fileToZip
        File filePathName
        String fileBaseName

        ArrayList fileInfo = new ArrayList<HashMap>()

        String zipFileID = UUID.randomUUID().toString();

        def zos = new ZipOutputStream( outputStream )
        byte[] readBuffer = new byte[2048];
        int bytesIn = 0

        //println fList.size()

       fList.each{ftzPath->

            fileToZip = new File(ftzPath)
            filePathName = new File(fileToZip?.parent)
            fileBaseName = fileToZip?.name.toLowerCase()


            if (!fileToZip.isDirectory()) {

                fileInfo.add(new HashMap([fileFullPath : "${ftzPath}", zipEntryPath : "${zipFileID}/${fileBaseName}"]))
            }
            else {
                filePathName.eachDirRecurse() { dir ->

                    def relPath = filePathName.toPath().relativize( dir.toPath() ).toFile()
                    String fPath = "${filePathName.toString()}/${relPath.toString()}"

                    new File(fPath).eachFile() { dfile ->
                        if (dfile.isDirectory() == false) {
                            //println dfile.getName()

                            String dFileFullPath = "${fPath}/${dfile.getName()}"
                            String dFileRelPath = "${relPath.toString()}/${dfile.getName()}"

                            fileInfo.add(new HashMap([fileFullPath : "${dFileFullPath}", zipEntryPath : "${zipFileID}/${dFileRelPath}"]))

                            }
                        }
                    }

                }
       }

        fileInfo.each{ zipFilePath->

           // println zipFilePath["fileFullPath"]

           FileInputStream fis = new FileInputStream( zipFilePath["fileFullPath"] );
           ZipEntry anEntry = new ZipEntry( "${zipFilePath["zipEntryPath"]}" );
           zos.putNextEntry( anEntry );

           while ( ( bytesIn = fis.read( readBuffer ) ) != -1 )
           {
                zos.write( readBuffer, 0, bytesIn );
            }

            fis.close();
        }

        zos.close();
    }




}


























