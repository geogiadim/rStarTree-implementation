import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class FilesHandler {
    private static final String DELIMITER = ",";
    private static final String PATH_TO_CSV = "./files/small-peraia.csv";
    private static final String PATH_TO_DATA_FILE = "./files/small-datafile.dat";
    private static final String PATH_TO_INDEX_FILE = "./files/small-indexfile.dat";

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

    /**
     *
     **/
    static void initializeDataFile(int dataDimensions) throws FileNotFoundException {
        try{
            FilesHandler.dataDimensions = dataDimensions;
            writeMetaDataBlock();
            // open buffer reader
            BufferedReader csvReader = (new BufferedReader(new FileReader(PATH_TO_CSV)));
            String stringRecord; // String used to read each line (row) of the csv file
            // calculate max records that can fit in a single block
            FilesHandler.maxRecordsInSingleBLock = calculateMaxRecordsInSingleBLock();
            System.out.println(maxRecordsInSingleBLock + " max records");
            // initialize the block array that contains records
            ArrayList<Record> block = new ArrayList<>();
            // set 0 value in total blocks variable
            FilesHandler.totalBlocksInDataFile = 1;
            // reading csv file
            while ((stringRecord = csvReader.readLine()) != null)
            {
                String[] row = stringRecord.split(DELIMITER); //Splitting the different attributes of the dataset

                long idRecord = Long.parseLong(row[0]);
                //Parsing the latitude and longitude to an arraylist
                ArrayList<Double> recordsCoordinates = new ArrayList<>();
                for (int k=1; k < row.length ; k++ ){
                    recordsCoordinates.add(Double.parseDouble(row[k]));
                }

                // create and add records in block
                block.add(new Record(idRecord,recordsCoordinates));

                /* check if block's array length is equal to max fitting records
                then write the block in data file and creates new block arraylist for the next one */
                if (block.size() == maxRecordsInSingleBLock ){
                    writeBlockInDataFile(block);
                    block = new ArrayList<>();
                    totalBlocksInDataFile++;
                }
            }
            // check if there are remaining records and add them in last one block
            if (block.size() > 0 ){
                writeBlockInDataFile(block);
                totalBlocksInDataFile++;
            }
            System.out.println(totalBlocksInDataFile + " total blocks");
            csvReader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * */
    private static int calculateMaxRecordsInSingleBLock(){
        ArrayList<Record> block = new ArrayList<>();
        int recCounter = 0;
        do{
            //create dummy records
            ArrayList<Double> coordinates = new ArrayList<>();
            for (int d=0; d < dataDimensions; d++){
                coordinates.add(0.0);
            }
            Record record = new Record(0,coordinates);
            block.add(record);

            // initialize byte arrays in order to store the serialized records
            byte[] recordToBytes = new byte[0];
            byte[] realRecordBytes = new byte[0];
            try {
                // serialize records to be able to be written in the .dat file
                recordToBytes = serialize(block);
                realRecordBytes = serialize(recordToBytes.length);
            } catch (IOException e){
                e.printStackTrace();
            }
            if (realRecordBytes.length + recordToBytes.length > BLOCK_SIZE){
                //flag = false;
                break;
            }
            recCounter ++;
        }while (true);

        return recCounter;
    }

    /**
     *
     *
     **/
    static void writeMetaDataBlock(){
        try {
            // Properties of metadata
            ArrayList<Integer> metadata = new ArrayList<>();
            metadata.add(dataDimensions);
            metadata.add(totalBlocksInDataFile);
            metadata.add(BLOCK_SIZE);
            metadata.add(maxRecordsInSingleBLock);

            //Serializing metadata to be able to be written in the .dat file
            byte[] metadataToBytes = serialize (metadata);
            byte[] realMetadataBytes = serialize(metadataToBytes.length);

            //Putting the serialized metadata to a new empty block array
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(realMetadataBytes, 0, block, 0, realMetadataBytes.length);
            System.arraycopy(metadataToBytes, 0, block, realMetadataBytes.length, metadataToBytes.length);

            //Writing the block array to the .dat file
            RandomAccessFile raf = new RandomAccessFile(new File(PATH_TO_DATA_FILE), "rw");
            raf.write(block);
            raf.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * */
    private static void writeBlockInDataFile(ArrayList<Record> block) {
        try {
            //Serializing data to be able to be written in the .dat file
            byte[] blockToBytes = serialize(block);
            byte[] realBlockBytes = serialize(blockToBytes.length);

            //Putting the serialized data to a new empty block array
            byte[] newBlock = new byte[BLOCK_SIZE];
            System.arraycopy(realBlockBytes, 0, newBlock, 0, realBlockBytes.length);
            System.arraycopy(blockToBytes, 0, newBlock, realBlockBytes.length, blockToBytes.length);

            //Writing the block array to the .dat file
            FileOutputStream fos = new FileOutputStream(PATH_TO_DATA_FILE,true);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(newBlock);
            bos.close();
            bos.flush();
            fos.close();
            fos.flush();

            //update metadata block
            writeMetaDataBlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * */
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


    /**
     *
     **/
    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    /**
     *
     * */
    private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
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