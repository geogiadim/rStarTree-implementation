import java.io.*;
import java.util.ArrayList;

public class FilesHandler {
    private static final String DELIMITER = ",";
    private static final String PATH_TO_CSV = "./files/small-peraia.csv";
    private static final String PATH_TO_DATA_FILE = "./files/small-datafile.dat";
    private static final String PATH_TO_INDEX_FILE = "small-indexfile.dat";

    private static final int BLOCK_SIZE = 2 * 1024;
    private static int dataDimensions;

    private static int totalBlocksInDataFile;
    private static int totalBlocksInIndexFile;

    static String getPathToCsv() {
        return PATH_TO_CSV;
    }

    static String getPathToDataFile(){
        return PATH_TO_DATA_FILE;
    }

    static String getPathToIndexFile(){
        return PATH_TO_INDEX_FILE;
    }

    static String getDelimiter() {
        return DELIMITER;
    }

    static int getBlockSize(){
        return BLOCK_SIZE;
    }

    static int getDataDimensions() {
        return dataDimensions;
    }

    static int getTotalBlocksInIndexFile() {
        return totalBlocksInIndexFile;
    }

    static int getTotalBlocksInDataFile(){
        return totalBlocksInDataFile;
    }



    // Calculates the total blocks in the datafile
    // Reads the data from the CSV files and adds it to the datafile
    static void initializeDataFile(int dataDimensions){
        try{
            FilesHandler.dataDimensions = dataDimensions;
            BufferedReader csvReader = (new BufferedReader(new FileReader(PATH_TO_CSV)));
            String stringRecord; // String used to read each line (row) of the csv file

            int maxRecordsInSingleBLock = (int) (BLOCK_SIZE / ((dataDimensions+1) * 8));

            ArrayList<Record> block = new ArrayList<>();
            int numberOfBlocks = 0;
            while ((stringRecord = csvReader.readLine()) != null)
            {
                String[] row = stringRecord.split(DELIMITER);
                long idRecord = Long.parseLong(row[0]);
                ArrayList<Double> recordsCoordinates = new ArrayList<>();
                for (int k=1; k < row.length ; k++ ){
                    recordsCoordinates.add(Double.parseDouble(row[k]));
                }

                block.add(new Record(idRecord,recordsCoordinates));

                if (block.size() == maxRecordsInSingleBLock ){
                    writeBlockInDataFile(block);
                    block = new ArrayList<>();
                    numberOfBlocks++;
                }

            }

            if (block.size() > 0 ){
                writeBlockInDataFile(block);
                numberOfBlocks++;
            }
            System.out.println(numberOfBlocks);
            csvReader.close();
        }catch(Exception e){e.printStackTrace();}
    }


    private static void writeBlockInDataFile(ArrayList<Record> block) {
        try {
            FileOutputStream fos = new FileOutputStream(PATH_TO_DATA_FILE,true);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(block);
            oos.close();
            fos.close();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
//        try {
//            byte[] recordInBytes = serialize(block);
//            byte[] goodPutLengthInBytes = serialize(recordInBytes.length);
//            byte[] blockInDataFile = new byte[BLOCK_SIZE];
//            System.arraycopy(goodPutLengthInBytes, 0, blockInDataFile, 0, goodPutLengthInBytes.length);
//            System.arraycopy(recordInBytes, 0, blockInDataFile, goodPutLengthInBytes.length, recordInBytes.length);
//
//            FileOutputStream fos = new FileOutputStream(PATH_TO_DATA_FILE,true);
//            BufferedOutputStream bout = new BufferedOutputStream(fos);
//            bout.write(blockInDataFile);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
}















//            if ( dataFile.exists() )
//            {
//                ArrayList<Integer> dataFileMetaData = readMetaDataBlock(PATH_TO_DATA_FILE);
//                if (dataFileMetaData == null)
//                    throw new IllegalStateException("Does not found metadata");
//
//                FilesHandler.dataDimensions = dataFileMetaData.get(0);
//                if (getDataDimensions()  <= 0)
//                    throw new IllegalStateException("Negative number of dimensions is not accepted!");
//
//                if (dataFileMetaData.get(1) != BLOCK_SIZE)
//                    throw new IllegalStateException("Wrong block size");
//
//                FilesHandler.totalBlocksInDataFile = dataFileMetaData.get(2);
//                if (getTotalBlocksInDataFile() < 0)
//                    throw new IllegalStateException("Negative number of blocks is not accepted!");
//            }