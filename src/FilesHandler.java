import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

/**
 *  This class handles all the read/write processes in/ from data and index file .
 */
public class FilesHandler {
    private static final String DELIMITER = ",";
    private static final String PATH_TO_CSV = "./files/small-peraia.csv";
    private static final String PATH_TO_DATA_FILE = "./files/small-datafile.dat";
    private static final String PATH_TO_INDEX_FILE = "./files/small-indexfile.dat";

    private static int dataDimensions;
    private static final int BLOCK_SIZE = 2 * 1024;
    private static int totalBlocksInDataFile;
    private static int maxRecordsInSingleBLock;

    private static int totalBlocksInIndexFile;
    private static int levelsOfRStarTree;

    static String getDelimiter() {return DELIMITER;}
    static String getPathToCsv() {return PATH_TO_CSV;}
    static String getPathToDataFile(){return PATH_TO_DATA_FILE;}
    static String getPathToIndexFile(){return PATH_TO_INDEX_FILE;}
    static int getDataDimensions() {return dataDimensions;}
    static int getBlockSize(){return BLOCK_SIZE;}
    static int getTotalBlocksInDataFile(){return totalBlocksInDataFile;}
    static int getMaxRecordsInSingleBLock(){return maxRecordsInSingleBLock;}
    static int getTotalBlocksInIndexFile() {return totalBlocksInIndexFile;}
    static int getLevelsOfRStarTree(){return levelsOfRStarTree;}

    /**
     * initialize the data file
     */
    static void initializeDataFile(int dataDimensions) {
        try{
            // delete data file if already exist from previous executions
            Files.deleteIfExists(Paths.get(PATH_TO_DATA_FILE));

            FilesHandler.dataDimensions = dataDimensions;
            writeMetaDataBlock();
            // open buffer reader
            BufferedReader csvReader = (new BufferedReader(new FileReader(PATH_TO_CSV)));
            String stringRecord; // String used to read each line (row) of the csv file
            // calculate max records that can fit in a single block
            FilesHandler.maxRecordsInSingleBLock = calculateMaxRecordsInSingleBLock();
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
            csvReader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * calculate max records that fit in single block
     */
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
                break;
            }
            recCounter ++;
        }while (true);

        return recCounter;
    }

    /**
     * Write metadata in first block of data file
     **/
    private static void writeMetaDataBlock(){
        try {
            // Properties of metadata
            ArrayList<Integer> metadata = new ArrayList<>();
            metadata.add(dataDimensions);
            metadata.add(BLOCK_SIZE);
            metadata.add(totalBlocksInDataFile);
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
     * Read the first block from data file that contains metadata
     */
    static ArrayList<Integer> readMetaDataBlock(){
        try {
            // open input streams in order to read the file
            RandomAccessFile raf = new RandomAccessFile(new File(PATH_TO_DATA_FILE), "rw");
            FileInputStream fis = new FileInputStream(raf.getFD());
            BufferedInputStream bis = new BufferedInputStream(fis);

            // read the block from data file and write it in the new initialized block
            byte[] block = new byte[BLOCK_SIZE];
            // read the block from data file and write it in the new initialized block
            int result = bis.read(block,0, BLOCK_SIZE);

            byte[] realMetadataBytes = serialize(new Random().nextInt()); // Serializing an integer ir order to get the size of goodPutLength in bytes
            System.arraycopy(block, 0, realMetadataBytes, 0, realMetadataBytes.length);

            byte[] dataInBlock = new byte[(Integer)deserialize(realMetadataBytes)];
            System.arraycopy(block, realMetadataBytes.length, dataInBlock, 0, dataInBlock.length);

            return (ArrayList<Integer>)deserialize(dataInBlock);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * write a block with records in data file
     */
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
     * Read a specific block from data file depending on given block id
     */
    static ArrayList<Record>  readBlockInDataFile (int blockId){
        try {
            // check if block id number is valid
            if (blockId < 1 || blockId > totalBlocksInDataFile)
                throw new Exception("You try to read block that does not exist.");
            // open input streams in order to read the file
            RandomAccessFile raf = new RandomAccessFile(new File(PATH_TO_DATA_FILE), "rw");
            FileInputStream fis = new FileInputStream(raf.getFD());
            BufferedInputStream bis = new BufferedInputStream(fis);

            // go to demanded block inside data file
            raf.seek((long) blockId*BLOCK_SIZE);

            // initialize a block
            byte[] block = new byte[BLOCK_SIZE];
            // read the block from data file and write it in the new initialized block
            int result = bis.read(block,0, BLOCK_SIZE);

            byte[] realDataBytes = serialize(new Random().nextInt());
            System.arraycopy(block, 0, realDataBytes, 0, realDataBytes.length);

            byte[] dataInBlock = new byte[(Integer)deserialize(realDataBytes)];
            System.arraycopy(block, realDataBytes.length, dataInBlock, 0, dataInBlock.length);

            return (ArrayList<Record>) deserialize(dataInBlock);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Serialize data in order to write them in dat file
     */
    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        return bos.toByteArray();
    }

    /**
     * Deserialize data in order to read them from dat file
     */
    private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }
}