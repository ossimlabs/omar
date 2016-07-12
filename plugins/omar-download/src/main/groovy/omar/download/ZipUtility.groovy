package omar.download

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.util.UUID
import java.util.ArrayList

/**
 * Created by nroberts on 6/27/16.
 */
class ZipUtility {

    static def zipFiles(ArrayList files, OutputStream outputStream)
    {
        def zos = new ZipOutputStream( outputStream )
        byte[] readBuffer = new byte[2048];
        String zipFileID = UUID.randomUUID().toString();
        int bytesIn = 0

        files.each{file->
           // println file
            FileInputStream fis = new FileInputStream( file );
            //create a new zip entry

            String baseName = file.name

            if(baseName.toLowerCase() == "a.toc")
            {
                // COLLAPSE UUID/mainFile

                // loop all files
                // make relative using the root UUID/
            }
            else
            {
                // collapse to UUID/file
            }
            ZipEntry anEntry = new ZipEntry( "${zipFileID}/${file.getName()}" );

            //place the zip entry in the ZipOutputStream object
            zos.putNextEntry( anEntry );

            while ( ( bytesIn = fis.read( readBuffer ) ) != -1 )
            {
                zos.write( readBuffer, 0, bytesIn );
            }

            fis.close();
        }

        zos.close();

    }

    static def zipDir( String dir2zip, String outputFile, String prefix = null )
    {
        String zipFileID = UUID.randomUUID().toString();
        String zipFile = "${outputFile}/${zipFileID}.zip"
        def zos = new ZipOutputStream( new FileOutputStream( zipFile ) )
        ZipUtility.zipDir( dir2zip, zipFileID, zos, prefix );
        zos.close();
    }

    static def zipDir( String dir2zip, String zipFileID, ZipOutputStream zos, String prefix = null )
    {
        def result = true
        try
        {
            //create a new File object based on the directory we
            //zipDirTemp: Is the location the files to be zipped are located
            def zipDirTemp = new File( dir2zip, prefix ?: "" );

            //get a listing of the directory content
            String[] dirList = zipDirTemp.list();

            byte[] readBuffer = new byte[2048];
            int bytesIn = 0
            //loop through dirList, and zip the files
            for ( int i = 0; i < dirList.length; i++ )
            {
                File f = new File( zipDirTemp, dirList[i] );
                if ( f.isDirectory() )
                {
                    //if the File object is a directory, call this
                    //function again to add its content recursively
                    String filePath = f.getPath();
                    zipDir( filePath, zos )
                    //loop again
                    continue;
                }

                //if we reached here, the File object f was not a directory
                //create a FileInputStream on top of f
                FileInputStream fis = new FileInputStream( f );
                //create a new zip entry

                ZipEntry anEntry = new ZipEntry( "${zipFileID}/${f.getName()}" );

                //place the zip entry in the ZipOutputStream object
                zos.putNextEntry( anEntry );
                //now write the content of the file to the ZipOutputStream
                while ( ( bytesIn = fis.read( readBuffer ) ) != -1 )
                {
                    zos.write( readBuffer, 0, bytesIn );
                }
                //close the Stream
                fis.close();
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace()
            result = false;
        }
    }
}
