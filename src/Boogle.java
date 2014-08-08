import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

interface Position
{
    int getRow( );
    int getCol( );
    List<Position> getNeighbors( );
    char getValue( ); 
}

/** Boogle game program
 * 
 * @author Roberto Arciniegas
 */
public class Boogle
{
    
    private class MyPosition implements Position
    {
        /**
         * 
         * @param r row
         * @param c column
         */
        public MyPosition( int r, int c )
        { row = r; col = c; }
        
        /**
         * 
         * @return a list with the neighbor cells
         */
        public List<Position> getNeighbors( )
        {
            int lowRow = ( row == 0 ) ? 0 : ( row - 1 );
            int lowCol = ( col == 0 ) ? 0 : ( col - 1 );
            int highRow = ( row == numRows - 1 ) ? row : ( row + 1 );
            int highCol = ( col == numCols - 1 ) ? col : ( col + 1 );
            
            List<Position> result = new ArrayList<>( );
            
            for( int r = lowRow; r <= highRow; ++r )
                for( int c = lowCol; c <= highCol; ++c )
                    if( r != row || c != col )
                        result.add( newPosition( r, c ) );
            
            return result;
        }
        
        /**
         * 
         * @return the row
         */
        public int getRow( )
        { return row; }
        
        /**
         * 
         * @return the column
         */
        public int getCol( )
        { return col; }
        
        /**
         * 
         * @return the char in the position
         */
        public char getValue( )
        { return grid[ row ][ col ]; }
        
        public String toString( )
        { return "(" + row + "," + col + ")"; }
        
        private int row;
        private int col;
    }
    
    public Boogle( List<String> puzzle )
    {
        int rows = puzzle.size();
        int cols = puzzle.get(0).length();
        
        grid = new char[ rows ][ cols ];
        positions = new Position[ rows ][ cols ];
        
        for( int i = 0; i < rows; ++i )
            grid[ i ] = puzzle.get( i ).toCharArray();
            
        numRows = rows;
        numCols = cols;
    }
    
    public String toString( )
    {
        StringBuilder sb = new StringBuilder( );
        
        for( int i = 0; i < numRows; ++i )
        {
            for( int j = 0; j < numCols; ++j )
                sb.append( grid[ i ] [ j ]);
            sb.append( '\n' );
        }
        
        return new String( sb );
    }

    /**
     * Driver routine to solve the Boggle game.
     * @return a Map containing the strings as keys, and the positions used
     *     to form the string (as a List) as values
     */
    public Map<String,List<Position>> solve( ) throws FileNotFoundException, IOException
    {
        Map<String,List<Position>> results = new TreeMap<>( );
        List<Position> path = new ArrayList<>( );
        
        FileReader fin = new FileReader( "dict.txt" );
        BufferedReader in = new BufferedReader( fin );
        
        String oneLine;
        
        while( ( oneLine = in.readLine( ) ) != null )
                dictionary.add( oneLine );
        
        Collections.sort(dictionary);
        
        for( int r = 0; r < numRows; r++ )
            for( int c = 0; c < numCols; c++ )
                solve( newPosition( r, c ), "", path, results );
        

        Iterator it = results.entrySet().iterator();
        int totalWords = 0, totalPoints = 0;
        int [ ] pointScale = {0, 0, 0, 1, 2, 3, 4, 6, 10, 15};
        if (results.size() > 200)
        {
            int [ ] pointsByLength = { 0, 0, 0, 0, 0, 0, 0, 0, 0};
            int [ ] wordsByLength = { 0, 0, 0, 0, 0, 0, 0, 0, 0};
            while (it.hasNext()) 
            {
                Map.Entry pairs = (Map.Entry)it.next();
                int wordLength = pairs.getKey().toString().length();
                if ( wordLength > 2)
                {
                    if (wordLength > 9)
                        wordLength = 9;
                    if (wordLength > 7)
                        System.out.println(pairs.getKey() + " \t " + pointScale[wordLength] + 
                            " points at positions " + pairs.getValue());
                    else
                    {
                        pointsByLength[wordLength] += pointScale[wordLength];
                        wordsByLength[wordLength]++;
                    }
                    totalWords ++;
                    if (wordLength > 9)
                        totalPoints += 15; 
                    else
                        totalPoints += pointScale[wordLength];
                } 
            }
            for (int i = 3; i < 8; i++)
                System.out.println(wordsByLength[i] + " words of length " + i +
                        ": " + pointsByLength[ i ] + " points");
            System.out.println("Total words = " + totalWords);
            System.out.println("Total points = " + totalPoints);
        }
        else
        {
            while (it.hasNext()) 
            {
                Map.Entry pairs = (Map.Entry)it.next();
                int wordLength = pairs.getKey().toString().length();
                if ( wordLength > 2)
                {
                    if (wordLength > 9)
                        wordLength = 9;
                    System.out.println(pairs.getKey() + " \t " + pointScale[wordLength] + 
                            " points at positions " + pairs.getValue());
                    totalWords ++;
                    if (wordLength > 9)
                        totalPoints += 15; 
                    else
                        totalPoints += pointScale[wordLength];
                } 
            }
            System.out.println("Total words = " + totalWords);
            System.out.println("Total points = " + totalPoints);
        }
        return results;
    }
    
    /**
     * Hidden recursive routine.
     * @param thisPos the current position
     * @param charSequence the characters in the potential matching string thusfar
     * @param path the List of positions used to form the potential matching string thusfar
     * @param results the Map that contains the strings that have been found as keys
     *       and the positions used to form the string (as a List) as values.
     */
   private void solve( Position thisPos, String charSequence, List path, Map results )
    {
        if (path.contains(thisPos))
            return;
        charSequence += thisPos.getValue();
        
        int index = Collections.binarySearch(dictionary, charSequence);
        //System.out.println(index);
        //System.out.println(dictionary.size());
        if ( (-index - 1) == dictionary.size())
            return;
              
        if (index > 0)
        {
            path.add(thisPos);
            results.put(charSequence, (new ArrayList(path)));
            //System.out.println(dictionary.get(index));
            for (Position p: thisPos.getNeighbors())
                solve( p, charSequence, path, results);
        }
        else if (dictionary.get(-index - 1).startsWith(charSequence))
        {
            path.add(thisPos);
            for (Position p: thisPos.getNeighbors())
                solve( p, charSequence, path, results);
        }

        path.remove(thisPos);
        return;
    }
    
    public Position newPosition( int r, int c )
    {
        if( positions[ r ][ c ] == null )
            positions[ r ][ c ] = new MyPosition( r, c );
        return positions[ r ][ c ];
    }
    
    private char [ ] [ ] grid; 
    private Position [ ] [ ] positions;
    private int numRows;
    private int numCols;
    List<String> dictionary = new ArrayList<>( );




    
    public static List<String> readWords( BufferedReader in ) throws IOException
    {
            String oneLine;
            List<String> lst = new ArrayList<>( );

            while( ( oneLine = in.readLine( ) ) != null )
                    lst.add( oneLine );

            return lst;
    } 
    
    public static void main( String [ ] args )
    {
        try
        {
            FileReader fin = new FileReader( args[ 0 ] );
            BufferedReader bin = new BufferedReader( fin );
            List<String> puzzle = readWords( bin );

            int rows = puzzle.size();
            boolean validGrid = true;

            for ( int i = 1; i < rows; i++)
                if (puzzle.get(i - 1).length() != puzzle.get( i ).length())
                    validGrid = false;
            
            if (validGrid)
            {
                Boogle w1 = new Boogle( puzzle );
                w1.solve();
            }
            else
                System.out.println("Invalid puzzle format.");
        }
        catch (FileNotFoundException exception)
        {
            System.out.println("Could not open the file. " + exception); 
        }
        catch (IOException exception)
        {
            System.out.println(exception);
        }
                
    }
}