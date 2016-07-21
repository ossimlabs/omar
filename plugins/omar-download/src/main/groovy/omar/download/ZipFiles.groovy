package omar.download

import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import omar.core.HttpStatus

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.util.UUID
import java.util.ArrayList
import omar.core.HttpStatus


/**
 * Created by nroberts on 7/7/16.
 */
@Slf4j
class ZipFiles {

    /***********************************************************
     *
     * Function: buildZipFileList
     * Purpose:  Takes a list of files/folders and creates zip
     *           entries for them.  It then returns an ArrayList
     *           of HashMaps containing the full path and the zip
     *           entry for the files
     *
     * @param    varFileList (ArrayList)
     * @return   fileInfo (ArrayList)
     *
     ***********************************************************/
    ArrayList buildZipFileList(String varRootFilePath, ArrayList varFileList)
    {
        File fileToZip
        File filePathName
        File relPath
        String dFileFullPath
        String zipEntryPath
        String zipFileID = UUID.randomUUID().toString();
        ArrayList fileInfo = new ArrayList<HashMap>()

        varFileList.each { ftzPath ->
            try
            {
                if(ftzPath!="")
                {
                    fileToZip = new File(ftzPath)

                    if (varRootFilePath) {
                        filePathName = new File(varRootFilePath)
                    } else {
                        filePathName = new File(fileToZip?.parent)
                    }

                    if (fileToZip.isDirectory()) {

                        fileToZip.eachFileRecurse { dir ->

                            if (!dir.isDirectory()) {
                                relPath = filePathName.toPath().relativize(dir.toPath()).toFile()
                                dFileFullPath = "${dir.toString()}"
                                zipEntryPath = "${zipFileID}/${relPath.toString()}"

                                fileInfo.add(new HashMap([fileFullPath: "${dFileFullPath}", zipEntryPath: "${zipEntryPath}"]))
                            }
                        }
                    } else {
                        dFileFullPath = "${fileToZip.toString()}"
                        relPath = filePathName.toPath().relativize(fileToZip.toPath()).toFile()
                        zipEntryPath = "${zipFileID}/${relPath.toString()}"

                        fileInfo.add(new HashMap([fileFullPath: "${dFileFullPath}", zipEntryPath: "${zipEntryPath}"]))
                    }
                }
                else
                {
                    log.error("The File Path Is Invalid")
                }
            }
            catch (e)
            {
                log.error(e.message.toString())
            }
        }

        return fileInfo
    }

    /****************************************************************
     * Function: zipSingle
     * Purpose:  Takes a list of files/folders, build their zip
     *           entries, then zip them up
     *
     * @param    varListOfFileInfo (HashMap)
     * @param    varOutputStream (OutputStream)
     *
     ****************************************************************/
    void zipSingle(HashMap varListOfFileInfo, OutputStream varOutputStream)
    {
        String rootDir = varListOfFileInfo["rootDirectory"]
        ArrayList fileList = varListOfFileInfo["files"]

        zip(buildZipFileList(rootDir, fileList), varOutputStream)
    }

    /****************************************************************
     * Function: zipMulti
     * Purpose:  Takes multiple list of files/folders, build their
     *           zip entries, then zip them up
     *
     * @param    varListOfFileInfo (ArrayList)
     * @param    varOutputStream (OutputStream)
     *
     ****************************************************************/
    void zipMulti(ArrayList varListOfFileInfo, OutputStream varOutputStream)
    {
        ArrayList fileInfo = new ArrayList()

        varListOfFileInfo.each { ftzPath ->

            String rootDir = ftzPath["rootDirectory"]
            ArrayList fileList = ftzPath["files"]

            fileInfo.addAll(buildZipFileList(rootDir, fileList))
        }
        zip(fileInfo, varOutputStream)
    }

    /****************************************************************
     * Function: zip
     * Purpose:  Takes a list of files and their zip entries, then
     *           zip them up
     *
     * @param    varFileInfo (ArrayList)
     * @param    varOutputStream (OutputStream)
     *
     ****************************************************************/
    void zip(ArrayList varFileInfo, OutputStream varOutputStream)
    {
        ZipOutputStream zos = new ZipOutputStream(varOutputStream)

        byte[] readBuffer = new byte[2048];
        int bytesIn = 0

        varFileInfo.each{ zipFilePath->

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
