
// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemListener;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.media.*;

// </editor-fold>


public class FileBrowserForm implements CommandListener, FileSystemListener, PlayerListener {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    // Специальная строка, обозначающая верхнюю папку.
    private static final String ВЕРХНЯЯ_ПАПКА = "..";

    // Специальная строка, которая обозначает корень.
    // Эта виртуальная папка содержит всё остальное дерево файловой системы.
    private static final String КОРЕНЬ = "/";

    // Разделитель строки пути по спецификации файловой системы.
    private static final String SEP_STR = "/";

    // separator character as defined by FC specification.
    private static final char SEP = '/';
    private String ИмяТекущПапки;

    private ChoiceGroup ПолеВыбора;

    public String РезультатСтрока;
    public int РезультатЧисло;

    private Command КомандаНазад = new Command ( "Назад", Command.BACK, 0 );
    private Command КомандаВыбрать = new Command ( "Выбрать", Command.ITEM, 1 );
    private Command КомандаСвойства = new Command ( "Свойства", Command.ITEM, 2 );

    private Image ИконкаПапки;
    private Image ИконкаФайла;

    List Проводник;

    private MIDlet Мидлет;
    private Display Дисплей;
    private Displayable РодительскоеОкно;
    private Settings Настройки;

    // </editor-fold>

    // <editor-fold desc=" Конструктор ">

    public FileBrowserForm( MIDlet мидлет, Displayable parent, Settings Настройки ) {

        Мидлет = мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = parent;
        this.Настройки = Настройки;

        try { main.ОбновитьПлеер( main.Плеер ); } catch( MediaException me ) {}

        ЗагрузкаИконок();

        ИмяТекущПапки = КОРЕНЬ;

        // Создаём главное окно.
        Проводник = new List( ИмяТекущПапки, List.IMPLICIT );

        Проводник.addCommand( КомандаСвойства );
        Проводник.addCommand( КомандаНазад );

        Проводник.setCommandListener( this );
        FileSystemRegistry.addFileSystemListener( this );

        main.Логгер.info( "[FileBrowserForm.java]: FileBrowserForm()" );
    }


    public FileBrowserForm( MIDlet мидлет, Displayable parent, Settings Настройки, ChoiceGroup ПолеВыбора ) {

        Мидлет = мидлет;
        Дисплей = Display.getDisplay( Мидлет );
        РодительскоеОкно = parent;
        this.Настройки = Настройки;
        this.ПолеВыбора = ПолеВыбора;

        ЗагрузкаИконок();

        ИмяТекущПапки = КОРЕНЬ;

        try { main.ОбновитьПлеер( main.Плеер ); } catch( MediaException me ) {}

        // Создаём главное окно.
        Проводник = new List( ИмяТекущПапки, List.IMPLICIT );

        Проводник.addCommand( КомандаВыбрать );
        Проводник.addCommand( КомандаНазад );

        Проводник.setCommandListener( this );
        FileSystemRegistry.addFileSystemListener( this );

        main.Логгер.info( "[FileBrowserForm.java]: FileBrowserForm()" );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void Отобразить() {

        ПоказатьТекущуюПапку();
        Дисплей.setCurrent( Проводник );
    }


    public void ЗагрузкаИконок() {

        try {

            ИконкаПапки = Image.createImage( "/icons/dir.png" );
            ИконкаФайла = Image.createImage( "/icons/file.png" );

        } catch ( IOException ex ) {

            main.Логгер.error( "[FileBrowserForm.java]: Иконки не загружены: " + ex.getMessage() );

            ИконкаПапки = null;
            ИконкаФайла = null;
        }

    }


    void ПроигратьЗвуковойФайл( String ПутькФайлу ) {

        final String Путь = ПутькФайлу;
        final FileBrowserForm врм = this;

        new Thread (

            new Runnable() {

                public void run() {

                    main.ПроигратьФайл( врм, Путь, Настройки.Основные.Громкость );
                }

            }

        ).start();

    }


    // Показать список файлов в текущей папке.
    // Выполняется в отдельном потоке, чтобы не блокировать системные сообщения.
    void ПоказатьТекущуюПапку() {

        new Thread(

            new Runnable() {

                public void run() { ПоказатьТекущуюПапкуСейчас(); }

            }

        ).start();
    }


    // Показать список файлов в текущей папке.
    void ПоказатьТекущуюПапкуСейчас() {

        Enumeration Перечисление;
        FileConnection Файлы = null;

        try {

            if ( КОРЕНЬ.equals( ИмяТекущПапки ) ) {

                Перечисление = FileSystemRegistry.listRoots();
                Проводник.deleteAll();
                Проводник.setTitle( ИмяТекущПапки );
                //Проводник.removeCommand( КомандаСвойства );
                Проводник.append( ВЕРХНЯЯ_ПАПКА, ИконкаПапки );

            } else {

                Файлы = ( FileConnection ) Connector.open( "file:///" + ИмяТекущПапки );
                Перечисление = Файлы.list();
                Проводник.deleteAll();
                Проводник.setTitle( ИмяТекущПапки );
                // Если не корень, то отображаем верхнюю папку
                Проводник.append( ВЕРХНЯЯ_ПАПКА, ИконкаПапки );
            }

            while ( Перечисление.hasMoreElements() ) {

                String ИмяФайла = ( String ) Перечисление.nextElement ();

                if ( ИмяФайла.charAt( ИмяФайла.length() - 1 ) == SEP ) {

                    // Это папка.
                    Проводник.append( ИмяФайла, ИконкаПапки );

                } else {

                    // Это обычный файл.
                    Проводник.append( ИмяФайла, ИконкаФайла );
                }

            }

            if ( Файлы != null ) {

                Файлы.close();
            }


        } catch ( IOException ИсключениеВводаВывода ) {

            main.Логгер.error( "[FileBrowserForm.java]: Ошибка при вводе/выводе. "
                    + ИсключениеВводаВывода.getMessage() );
        }

    }


    void СменитьПапку( String ИмяПапки ) {

        try { main.ОбновитьПлеер( main.Плеер ); } catch( MediaException me ) {}

        // В случае папки просто меняем текущую и обновляем форму.
        if ( ИмяТекущПапки.equals( КОРЕНЬ ) ) {

            if ( ИмяПапки.equals( ВЕРХНЯЯ_ПАПКА ) ) {

                Дисплей.setCurrent( РодительскоеОкно );
                return;
            }

            ИмяТекущПапки = ИмяПапки;

        } else if ( ИмяПапки.equals( ВЕРХНЯЯ_ПАПКА ) ) {

            // Go up one directory.
            int i = ИмяТекущПапки.lastIndexOf( SEP, ИмяТекущПапки.length() - 2 );

            if ( i != -1 ) {

                ИмяТекущПапки = ИмяТекущПапки.substring( 0, i + 1 );

            }  else {

                ИмяТекущПапки = КОРЕНЬ;
            }

        } else {

            ИмяТекущПапки = ИмяТекущПапки + ИмяПапки;
        }

        ПоказатьТекущуюПапку();
    }


    void ПоказатьСвойства( String ИмяФайла ) {

        if ( ИмяФайла.equals( ВЕРХНЯЯ_ПАПКА ) ) return;

        ( new FilePropertiesForm( Мидлет, Проводник, ИмяТекущПапки, ИмяФайла ) ).Отобразить();

    }


    public void rootChanged( int state, String rootName ) {

        ПоказатьТекущуюПапку();
    }

    // </editor-fold>

    // <editor-fold desc=" Обработчики событий ">

    public void playerUpdate( Player player, String event, Object data ) {

        if ( ( event == END_OF_MEDIA ) || ( event == ERROR )
            || ( event == CLOSED ) || ( event == STOPPED ) ) {

            try { main.ОбновитьПлеер( player ); } catch( MediaException me ) {}
        }

    }


    public void commandAction( Command команда, Displayable элемент ) {

        if ( элемент == Проводник ) {

            List curr = ( List ) элемент;

            final String currFile = curr.getString( curr.getSelectedIndex() );

            // Обработка команд.
            if ( команда == List.SELECT_COMMAND ) {

                if ( currFile.endsWith( SEP_STR ) || currFile.equals ( ВЕРХНЯЯ_ПАПКА ) ) {

                    main.Логгер.info( "[FileBrowserForm.java]: -> " + ИмяТекущПапки + currFile );
                    СменитьПапку( currFile );

                } else {

                    main.Логгер.info( "[FileBrowserForm.java]: " + "file:///" + ИмяТекущПапки + currFile );
                    ПроигратьЗвуковойФайл( "file:///" + ИмяТекущПапки + currFile );
                }


            } else if ( команда == КомандаСвойства ) {

                ПоказатьСвойства( currFile );
                main.Логгер.info( "[FileBrowserForm.java]: <Свойства>" );


            } else if ( команда == КомандаВыбрать ) {

                ПолеВыбора.set( 0, "file:///" + ИмяТекущПапки + currFile, null );
                ПолеВыбора.setSelectedIndex( 0, true );

                // Останавливаем и закрываем плеер.
                try { main.ОбновитьПлеер( main.Плеер ); } catch( MediaException me ) {}

                main.Логгер.info( "[FileBrowserForm.java]: " + "\"" + ПолеВыбора.getString(0) + "\"" );
                main.Логгер.info( "[FileBrowserForm.java]: <Выбрать>" );

                Дисплей.setCurrent( РодительскоеОкно );


            } else if ( команда == КомандаНазад ) {

                if ( ПолеВыбора != null ) ПолеВыбора.setSelectedIndex( 0, true );

                СменитьПапку( ".." );
                main.Логгер.info( "[FileBrowserForm.java]: <Назад>" );

            }

        }

    }

}