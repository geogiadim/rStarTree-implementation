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
    private static int maxRecordsInSingleBLock;
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

    static int getMaxRecordsInSingleBLock(){return maxRecordsInSingleBLock;}


    // Calculates the total blocks in the datafile
    // Reads the data from the CSV files and adds it to the datafile
    static void initializeDataFile(int dataDimensions) throws FileNotFoundException {
        try{
            FilesHandler.dataDimensions = dataDimensions;
            BufferedReader csvReader = (new BufferedReader(new FileReader(PATH_TO_CSV)));
            String stringRecord; // String used to read each line (row) of the csv file

            maxRecordsInSingleBLock = (int) (BLOCK_SIZE / ((dataDimensions+1) * 8));
            //maxRecordsInSingleBLock = calculateMaxRecordsInSingleBLock();
            System.out.println(maxRecordsInSingleBLock + " max records");
            //ArrayList<ArrayList<Record>> arrayOfBlocks = new ArrayList<ArrayList<Record>>() ;
            ArrayList<Record> block = new ArrayList<>();

            totalBlocksInDataFile = 0;
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
                    //arrayOfBlocks.add(block);
                    block = new ArrayList<>();
                    totalBlocksInDataFile++;
                    break;
                }
            }

            if (block.size() > 0 ){
                writeBlockInDataFile(block);
                //arrayOfBlocks.add(block);
                totalBlocksInDataFile++;
            }
            //System.out.println(totalBlocksInDataFile);
            //writeBlockInDataFile(arrayOfBlocks);
            csvReader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static int calculateMaxRecordsInSingleBLock(){
        ArrayList<Record> block = new ArrayList<>();

        int maxRecordsInSingleBLock = (int) (BLOCK_SIZE / ((dataDimensions+1) * 8));
        int remainingBytesInBlock = (int) (BLOCK_SIZE % ((dataDimensions+1) * 8 ));

        int recCounter;
        for (recCounter=0; recCounter < Integer.MAX_VALUE; recCounter++){
            ArrayList<Double> coordinates = new ArrayList<>();
            for (int d=0; d < dataDimensions; d++){
                coordinates.add(0.0);
            }
            Record record = new Record(0,coordinates);
            block.add(record);

            byte[] recordToBytes = new byte[0];
            byte[] goodPutLengthInBytes = new byte[0];
            try {
                recordToBytes = serialize(block);
                System.out.println(recordToBytes.length);
                goodPutLengthInBytes = serialize(recordToBytes.length);
                System.out.println(goodPutLengthInBytes.length);
            } catch (IOException e){
                e.printStackTrace();
            }
            if (goodPutLengthInBytes.length + recordToBytes.length > BLOCK_SIZE)
                break;
        }
        return recCounter;
    }

    // Used to serializable a serializable Object to byte array
    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    private static void writeBlockInDataFile(ArrayList<Record> block) {
        try {

            FileOutputStream fos = new FileOutputStream(PATH_TO_DATA_FILE,true);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            //oos.writeObject(block);
            for (Record record : block) {
                oos.writeLong(record.getSlotId());
                for (Double coordinate : record.getRecordsCoordinates()) {
                    oos.writeDouble(coordinate);
                }
            }
            oos.close();
            fos.close();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
//        try {
//            byte[] recordInBytes = serialize(block);
//            byte[] goodPutLengthInBytes = serialize(recordInBytes.length);
//            byte[] blockToBytes = new byte[BLOCK_SIZE];
//            System.arraycopy(goodPutLengthInBytes, 0, blockToBytes, 0, goodPutLengthInBytes.length);
//            System.arraycopy(recordInBytes, 0, blockToBytes, goodPutLengthInBytes.length, recordInBytes.length);
//
//            FileOutputStream fos = new FileOutputStream(PATH_TO_DATA_FILE,true);
//            BufferedOutputStream bout = new BufferedOutputStream(fos);
//            bout.write(blockToBytes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    static void readBlockInDataFile (int blockId){
        ArrayList<ArrayList<Record>> newRecords;
        try
        {
            FileInputStream fis = new FileInputStream(PATH_TO_DATA_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);

            newRecords = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
            ArrayList<Record> block = newRecords.get(blockId);
            for (Record rec: block){
                System.out.println(rec.getRecordsCoordinates());
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }
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


//    private static byte[] serialize(Object obj) throws IOException {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        ObjectOutputStream os = new ObjectOutputStream(out);
//        os.writeObject(obj);
//        return out.toByteArray();
//    }
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