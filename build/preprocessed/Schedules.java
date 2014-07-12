/*
 * Класс Schedules содержит в себе массив расписсаний, уроков и звонков,
 * а также все необходимые методы для работы с этими массивами.
 */

// <editor-fold defaultstate="collapsed" desc=" Подключаемые модули ">

import java.io.*;
import java.util.*;

import javax.microedition.midlet.*;
import javax.microedition.rms.*;
import javax.microedition.lcdui.*;

import j2me.rms.*;

// </editor-fold>

public class Schedules {

    // <editor-fold defaultstate="collapsed" desc=" Поля класса ">

    final public static byte ТИП_СВОБОДНЫЙ = 0;
    final public static byte ТИП_НАЧАЛО = 1;
    final public static byte ТИП_КОНЕЦ = 2;
    final public static byte ТИП_СМЕНА_РАСПИСАНИЯ = 3;

    final public static byte ТИП_ПЛАНА_ОСНОВНОЙ = 0;
    final public static byte ТИП_ПЛАНА_ДОПОЛНИТЕЛЬНЫЙ = 1;

    final public static byte ТИП_КОРЕНЬ = 0;
    final public static byte ТИП_ПЛАН = 1;
    final public static byte ТИП_РАСПИСАНИЕ = 2;
    final public static byte ТИП_УРОК = 3;
    final public static byte ТИП_ЗВОНОК = 4;

    public int цВыбранныйЭлемент;

    public long НачалоРаботы;
    public boolean ПервыеСутки;
    public long ДобавочнаяПоправка;

    public String Название;
    public Vector Расписания;
    public ОбразСпискаПланов СписокПланов;

    private MIDlet Мидлет;
    private Display Дисплей;
    public ОбразДанных Данные;
    Settings Настройки;

    // </editor-fold>

    public class ОбразПлана {
        public String Название;
        public Integer[][] Основной;
        public Integer[][] Дополнительный;

        ОбразПлана() {
            Название = "новый";
            Основной = new Integer[12][31];
            Дополнительный = new Integer[12][31];
        }
    }

    public class ОбразСпискаПланов {
        Vector Элементы;

        ОбразСпискаПланов() {
            Элементы = new Vector();
        }

        public void Очистить() {
            Элементы.removeAllElements();
        }
    }

    public class ОбразДанных {
        Vector Записи;

        ОбразДанных() {
            Записи = new Vector();
        }

        int Добавить( Record Запись ) {
            Записи.addElement( Запись );
            return ( Записи.size() - 1 );
        }

        void Удалить( int Номер ) {
            Записи.removeElementAt( Номер );
        }

        void Очистить() {
            Записи.removeAllElements();
        }

        void Изменить( int Номер, Record Запись ) {
            Записи.setElementAt( Запись, Номер);
        }

        Record ЗаписьN( int Номер ) {
            return ( Record ) Записи.elementAt( Номер );
        }

        Record ГруппаNЗаписьN( int ГруппаN, int ЗаписьN ) {
            Record Запись;

            int ii = 0;

            for ( Enumeration e = Данные.Записи.elements(); e.hasMoreElements(); ) {
                Запись = ( Record ) e.nextElement();
                if ( Запись.Верх == ГруппаN ) {
                    if ( ii == ЗаписьN ) {
                        return Запись;
                    } else {
                        ii++;
                    }
                }
            }

            return null;
        }
    }

    // Описание вспомогательных классов
    public class BellClass {
        public boolean Включен;
        public boolean Взведён;
        public boolean ПредЗнач;
        public byte Тип;
        public int цЧасы;
        public int цМинуты;
        public int цСекунды;
        public int цВыбранныйЭлемент; // для меню
        public String Путь;
        public String Путь2;
    }

    public class LessonClass {
        public int цВыбранныйЭлемент; // для меню
        public String Название;
        // Обязательные звонки (чтобы не искать по всему массиву)
        public BellClass ЗвонокПредвНач; // время устанавливается/изменяется автоматически
        public BellClass ЗвонокПредвКон;
        public BellClass ЗвонокНачало;
        public BellClass ЗвонокКонец;
        // Пользовательский набор звонков
        public Vector Звонки;

        public void Добавить( BellClass Звонок ) {
            Звонки.addElement( Звонок );
        }

        public void Удалить( int цНомер ) {
            Звонки.removeElementAt( цНомер );
        }

        public void Очистить() {
            Звонки.removeAllElements();
        }
    }

    public class ScheduleClass {
        public int цВыбранныйЭлемент; // для меню
        public String Название;
        public Vector Уроки;

        public void Добавить( LessonClass Урок ) {
            Уроки.addElement( Урок );
        }

        public void Удалить( int цНомер ) {
            Уроки.removeElementAt( цНомер );
        }

        public void Очистить() {
            Уроки.removeAllElements();
        }
    }

    // <editor-fold desc=" Конструктор ">

    public Schedules( MIDlet Мидлет, Settings Настройки ) {

        this.Мидлет = Мидлет;
        this.Настройки = Настройки;
        Дисплей = Display.getDisplay( Мидлет );

        // Время старта программы
        // Используется для корректировки времени в программе
        НачалоРаботы = System.currentTimeMillis();

        СписокПланов = new ОбразСпискаПланов();
        Расписания = new Vector();
        Данные = new ОбразДанных();

        НачальныеНастройки();

        main.Логгер.info( "[Schedules.java]: Schedules()" );
    }

    // </editor-fold>

    // <editor-fold desc=" Методы класса ">

    public void НачальныеНастройки() {

        Очистить();

        цВыбранныйЭлемент = -1;
        ПервыеСутки = true;
        ДобавочнаяПоправка = 0;

        ДобавитьРасписание( "Пустое" );
        Настройки.Расписания.ТекущееРасписание = 0;

        Record Запись;

        Запись = new Record();
        Запись.Верх = 0;
        Запись.Имя = "Корень";
        Запись.ТипЗаписи = ТИП_КОРЕНЬ;
        Запись.ТипЗвонка = ТИП_СВОБОДНЫЙ;
        Запись.Часы = 0;
        Запись.Минуты = 0;
        Запись.Секунды = 0;
        Запись.ПутьОсновн = "";
        Запись.ПутьПредвар = "";
        Данные.Добавить( Запись );

        Запись = new Record();
        Запись.Верх = 0;
        Запись.Имя = "Пустое";
        Запись.ТипЗаписи = ТИП_РАСПИСАНИЕ;
        Запись.ТипЗвонка = ТИП_СВОБОДНЫЙ;
        Запись.Часы = 0;
        Запись.Минуты = 0;
        Запись.Секунды = 0;
        Запись.ПутьОсновн = "";
        Запись.ПутьПредвар = "";

        int N = Данные.Добавить( Запись );

        ОбразПлана План = new ОбразПлана();

        for ( int ii = 0; ii < 12; ii++ ) {

            for ( int jj = 0; jj < 31; jj++ ) {

                План.Основной[ii][jj] = new Integer(N);
                План.Дополнительный[ii][jj] = new Integer(N);
            }

        }

        СписокПланов.Элементы.addElement( План );

        Запись = new Record();
        Запись.Верх = 0;
        Запись.Имя = План.Название;
        Запись.ТипЗаписи = ТИП_ПЛАН;
        Запись.ТипЗвонка = ТИП_СВОБОДНЫЙ;
        Запись.Часы = 0;
        Запись.Минуты = 0;
        Запись.Секунды = 0;
        Запись.ПутьОсновн = "";
        Запись.ПутьПредвар = "";
        Данные.Добавить( Запись );

        Настройки.Расписания.ТекущийПлан = 0;
    }


    public void СохранитьРасписания( String ИмяЗаписи ) {

        RecordStore ТаблицаРасписаний = null;
        FieldList СписокПолей = new FieldList( 9 );
        FieldBasedStore ПолеЗапись;

        int индекс = 0; // Счётчик столбцов таблицы
        СписокПолей.setFieldType( индекс++, FieldList.TYPE_INT ); // ТипЗаписи
        СписокПолей.setFieldType( индекс++, FieldList.TYPE_INT ); // ТипЗвонка
        СписокПолей.setFieldType( индекс++, FieldList.TYPE_INT ); // Часы
        СписокПолей.setFieldType( индекс++, FieldList.TYPE_INT ); // Минуты
        СписокПолей.setFieldType( индекс++, FieldList.TYPE_INT ); // Секунды
        СписокПолей.setFieldType( индекс++, FieldList.TYPE_INT ); // Верх

        СписокПолей.setFieldType( индекс++, FieldList.TYPE_STRING ); // Имя
        СписокПолей.setFieldType( индекс++, FieldList.TYPE_STRING ); // ПутьОсновн
        СписокПолей.setFieldType( индекс++, FieldList.TYPE_STRING ); // ПутьПредвар

        Record Запись;

        try {

            String[] Список = RecordStore.listRecordStores();

            if ( Список != null ) {

                for ( int ii = 0; ii < Список.length; ii++ ) {

                    if ( ИмяЗаписи.equals( Список[ii] ) ) {

                        RecordStore.deleteRecordStore( ИмяЗаписи );
                    }

                }

            }

            ТаблицаРасписаний = RecordStore.openRecordStore( ИмяЗаписи, true );
            СписокПолей.toRecordStore( ТаблицаРасписаний, -1 );
            ПолеЗапись = new FieldBasedStore( ТаблицаРасписаний, СписокПолей );

            Object[] Объекты = new Object[9];

            for ( int ii = 0; ii < Данные.Записи.size(); ii++ ) {

                Запись = ( Record ) Данные.Записи.elementAt(ii);

                Объекты[0] = new Integer( Запись.ТипЗаписи );
                Объекты[1] = new Integer( Запись.ТипЗвонка );
                Объекты[2] = new Integer( Запись.Часы );
                Объекты[3] = new Integer( Запись.Минуты );
                Объекты[4] = new Integer( Запись.Секунды );
                Объекты[5] = new Integer( Запись.Верх );

                Объекты[6] = new String( Запись.Имя );
                Объекты[7] = new String( Запись.ПутьОсновн );
                Объекты[8] = new String( Запись.ПутьПредвар );

                ПолеЗапись.addRecord( Объекты );
            }

            ТаблицаРасписаний.closeRecordStore();

        } catch( RecordStoreException ОшибкаХранилища ){

            main.Логгер.info( "[Schedules.java]: Ошибка добавления записи " + ОшибкаХранилища.getMessage() );
            return;

        } catch( IOException ОшибкаВводаВывода ){

            main.Логгер.info( "[Schedules.java]: Ошибка записи поля " + ОшибкаВводаВывода.getMessage() );
            return;
        }

    }


    public void ЗагрузитьРасписания( String ИмяЗаписи ) {

        RecordStore ТаблицаРасписаний = null;
        FieldBasedStore ПолеЗапись;
        Object[] Объекты;
        Record Запись;

        main.Логгер.info( "[Schedules.java]: ЗагрузитьРасписания(). Начало" );

        Очистить();

        try {
            String[] Список = RecordStore.listRecordStores();
            if ( Список == null ) return;

            ТаблицаРасписаний = RecordStore.openRecordStore( ИмяЗаписи, false );
            if ( ТаблицаРасписаний == null ) return;
            if ( ТаблицаРасписаний.getNumRecords() < 2 ) return;

            ПолеЗапись = new FieldBasedStore( ТаблицаРасписаний );

            for ( int ii = 2; ii <= ТаблицаРасписаний.getNumRecords(); ii++ ) {
                Объекты = ПолеЗапись.readRecord(ii);
                Запись = new Record();
                Запись.ТипЗаписи = ( ( Integer ) Объекты[0] ).intValue();
                Запись.ТипЗвонка = ( ( Integer ) Объекты[1] ).intValue();
                Запись.Часы = ( ( Integer ) Объекты[2] ).intValue();
                Запись.Минуты = ( ( Integer ) Объекты[3] ).intValue();
                Запись.Секунды = ( ( Integer ) Объекты[4] ).intValue();
                Запись.Верх = ( ( Integer ) Объекты[5] ).intValue();

                Запись.Имя = ( String ) Объекты[6];
                Запись.ПутьОсновн = ( String ) Объекты[7];
                Запись.ПутьПредвар = ( String ) Объекты[8];

                Данные.Записи.addElement( Запись );
            }

            ТаблицаРасписаний.closeRecordStore();
        }
        catch( RecordStoreException e ){
            main.Логгер.info( "[Schedules.java]: Ошибка при загрузке расписания: " + ИмяЗаписи );
            return;
        }
        catch( IOException e ){
            main.Логгер.info( "[Schedules.java]: Ошибка при сохранении списка полей." );
            return;
        }

        // Копирование записей в память
        ScheduleClass Расписание;
        LessonClass Урок;
        BellClass Звонок;

        for ( int ii = 0; ii < Данные.Записи.size(); ii++ ) {
            Запись = ( Record ) Данные.Записи.elementAt(ii);

            if ( Запись.ТипЗаписи == ТИП_РАСПИСАНИЕ ) {
                // Добавляем одно расписание
                Расписание = new ScheduleClass();
                Расписание.Название = Запись.Имя;
                Расписание.Уроки = new Vector();
                Расписание.цВыбранныйЭлемент = ii;
                цВыбранныйЭлемент = 0;
                Расписания.addElement( Расписание );
            }

            if ( Запись.ТипЗаписи == ТИП_ПЛАН ) {
                Parser.ОбразРезультата врм, Месяц, Число;
                Vector век, век2;
                врм = null;
                Parser Разборщик;

                ОбразПлана План = new ОбразПлана();
                План.Название = Запись.Имя;

                Разборщик = new Parser( Мидлет, Настройки, Запись.ПутьОсновн );
                try {
                    Разборщик.ПолучитьЭлемент();
                    врм = Разборщик.СложениеВычитание( false );
                } catch ( Exception Ошибка ) {
                    main.Логгер.info( "[Schedules.java]: Ошибка при разборе массива основного плана." + Ошибка.getMessage() );
                }

                if ( врм == null ) continue;

                век = ( Vector ) врм.Значение.elementAt(0);
                врм = ( Parser.ОбразРезультата ) век.elementAt(0);
                век = ( Vector ) врм.Значение.elementAt(0);

                for ( int jj = 0; jj < 12; jj++ ) {
                    врм = ( Parser.ОбразРезультата ) век.elementAt(jj);
                    век2 = ( Vector ) врм.Значение.elementAt(0);
                    Месяц = ( Parser.ОбразРезультата ) век2.elementAt(0);
                    век2 = ( Vector ) Месяц.Значение.elementAt(0);

                    for ( int k = 0; k < 31; k++ ) {
                        Число = ( Parser.ОбразРезультата ) век2.elementAt(k);
                        План.Основной[jj][k] = ( Integer ) Число.Значение.elementAt(0);
                    }
                }

                Разборщик = new Parser( Мидлет, Настройки, Запись.ПутьПредвар );
                try {
                    Разборщик.ПолучитьЭлемент();
                    врм = Разборщик.СложениеВычитание( false );
                } catch ( Exception Ошибка ) {
                    main.Логгер.info( "[Schedules.java]: Ошибка при разборе массива дополнительного плана." + Ошибка.getMessage() );
                }

                if ( врм == null ) continue;

                век = ( Vector ) врм.Значение.elementAt(0);
                врм = ( Parser.ОбразРезультата ) век.elementAt(0);
                век = ( Vector ) врм.Значение.elementAt(0);

                for ( int jj = 0; jj < 12; jj++ ) {
                    врм = ( Parser.ОбразРезультата ) век.elementAt(jj);
                    век2 = ( Vector ) врм.Значение.elementAt(0);
                    Месяц = ( Parser.ОбразРезультата ) век2.elementAt(0);
                    век2 = ( Vector ) Месяц.Значение.elementAt(0);

                    for ( int k = 0; k < 31; k++ ) {
                        Число = ( Parser.ОбразРезультата ) век2.elementAt(k);
                        План.Дополнительный[jj][k] = ( Integer ) Число.Значение.elementAt(0);
                    }
                }

                СписокПланов.Элементы.addElement( План );
            }
        }

        for ( int ii = 0; ii < Расписания.size(); ii++ ) {
            Расписание = ( ScheduleClass ) Расписания.elementAt(ii);

            for ( int jj = 0; jj < Данные.Записи.size(); jj++ ) {
                Запись = ( Record ) Данные.Записи.elementAt(jj);
                if ( Запись.Верх == Расписание.цВыбранныйЭлемент ) {
                    Урок = new LessonClass();
                    Урок.Название = Запись.Имя;
                    Урок.Звонки = new Vector();
                    Урок.цВыбранныйЭлемент = jj;

                    Расписание.Уроки.addElement( Урок );
                }
            }
        }

        for ( int ii = 0; ii < Расписания.size(); ii++ ) {
            Расписание = ( ScheduleClass ) Расписания.elementAt(ii);

            for ( int jj = 0; jj < Расписание.Уроки.size(); jj++ ) {
                Урок = ( LessonClass ) Расписание.Уроки.elementAt(jj);

                for ( int k = 0; k < Данные.Записи.size(); k++ ) {
                    Запись = ( Record ) Данные.Записи.elementAt(k);
                    if ( Запись.Верх == Урок.цВыбранныйЭлемент ) {
                        Звонок = new BellClass();
                        Звонок.Тип = ( byte ) Запись.ТипЗвонка;
                        Звонок.Взведён = false;
                        Звонок.Включен = true;
                        Звонок.ПредЗнач = false;
                        Звонок.Путь = Запись.ПутьОсновн;
                        Звонок.Путь2 = Запись.ПутьПредвар;
                        Звонок.цЧасы = Запись.Часы;
                        Звонок.цМинуты = Запись.Минуты;
                        Звонок.цСекунды = Запись.Секунды;
                        Звонок.цВыбранныйЭлемент = k;

                        Урок.Звонки.addElement( Звонок );
                    }
                }
            }
        }

        main.Логгер.info( "[Schedules.java]: ЗагрузитьРасписания(). Конец" );
    }


    public void ЭкспортРасписаний( String ИмяФайла, int Кодировка ) {

        switch ( Кодировка ) {

            case Settings.КОДИРОВКА_UTF8:

                main.Логгер.info( "[Schedules.java]: ЭкспортРасписаний( \""
                        + ИмяФайла + "\", UTF-8 ); // Начало" );
                break;

            case Settings.КОДИРОВКА_CP1251:
                
                main.Логгер.info( "[Schedules.java]: ЭкспортРасписаний( \""
                        + ИмяФайла + "\", CP-1251 ); // Начало" );
                break;
        }

        String Текст = "";

        // Сохранение настроек.
        Текст += "ЗВОНОК_СВОБОДНЫЙ = " + ТИП_СВОБОДНЫЙ + ";\n";
        Текст += "ЗВОНОК_НАЧАЛО = " + ТИП_НАЧАЛО + ";\n";
        Текст += "ЗВОНОК_ОКОНЧАНИЕ = " + ТИП_КОНЕЦ + ";\n";
        Текст += "\n";
        Текст += "Корень = " + ТИП_КОРЕНЬ + ";\n";
        Текст += "\n";
        Текст += "ГРУППА_РАСПИСАНИЕ = " + ТИП_РАСПИСАНИЕ + ";\n";
        Текст += "ГРУППА_УРОК = " + ТИП_УРОК + ";\n";
        Текст += "\n";

        ScheduleClass Расписание;
        LessonClass Урок;
        BellClass Звонок;

        for ( Enumeration e = Расписания.elements(); e.hasMoreElements(); ) {
            Расписание = ( ScheduleClass ) e.nextElement();

            Текст += Расписание.Название.replace( ' ', '_' ) + " = ДобавитьГруппу( Корень, ГРУППА_РАСПИСАНИЕ, \""
                    + Расписание.Название + "\" );\n";
        }
        Текст += "\n";

        for ( Enumeration e = Расписания.elements(); e.hasMoreElements(); ) {
            Расписание = ( ScheduleClass ) e.nextElement();

            for ( Enumeration e1 = Расписание.Уроки.elements(); e1.hasMoreElements(); ) {
                Урок = ( LessonClass ) e1.nextElement();
                Текст += Расписание.Название.replace( ' ', '_' ) + "."
                        + Урок.Название.replace( ' ', '_' )
                        + " = ДобавитьГруппу( "
                        + Расписание.Название.replace( ' ', '_' )
                        + ", ГРУППА_УРОК, \""
                        + Урок.Название + "\" );\n";
            }
            Текст += "\n";
        }

        for ( Enumeration e = Расписания.elements(); e.hasMoreElements(); ) {
            Расписание = ( ScheduleClass ) e.nextElement();

            for ( Enumeration e1 = Расписание.Уроки.elements(); e1.hasMoreElements(); ) {
                Урок = ( LessonClass ) e1.nextElement();

                for ( Enumeration e2 = Урок.Звонки.elements(); e2.hasMoreElements(); ) {
                    Звонок = ( BellClass ) e2.nextElement();
                    Текст += "ДобавитьЗвонок( " + Расписание.Название.replace( ' ', '_' )
                            + "." + Урок.Название.replace( ' ', '_' ) + ", "
                            + Integer.toString( Звонок.цЧасы ) + ", "
                            + Integer.toString( Звонок.цМинуты ) + ", ";

                    switch ( Звонок.Тип ) {
                        case ТИП_СВОБОДНЫЙ:
                            Текст += "ЗВОНОК_СВОБОДНЫЙ, ";
                            break;
                        case ТИП_НАЧАЛО:
                            Текст += "ЗВОНОК_НАЧАЛО, ";
                            break;
                        case ТИП_КОНЕЦ:
                            Текст += "ЗВОНОК_ОКОНЧАНИЕ, ";
                            break;
                    }

                    Текст += "\"" + Звонок.Путь + "\"" + ", " + "\"" + Звонок.Путь2 + "\" );\n" ;
                }
                Текст += "\n";
            }

        }

        // Сохранение планов, если они есть.
        if ( !СписокПланов.Элементы.isEmpty() ) {

            ОбразПлана План;

            // Создаём новые переменные для расписаний.
            for ( int ii = 0; ii < Расписания.size(); ii++ ) {

                Расписание = ( ScheduleClass ) Расписания.elementAt(ii);
                Текст += "Р" + (ii + 1) + " = " + Расписание.Название.replace( ' ', '_' ) + ";\n" ;
            }

            Текст += "\n";

            for ( int n = 0; n < СписокПланов.Элементы.size(); n++ ) {

                План = ( ОбразПлана ) СписокПланов.Элементы.elementAt(n);

                // Сохраняем основной план.
                Текст += "ОсновнойПлан =\n";
                Текст += "[\n";

                int N;
                for ( int ii = 0; ii < 12; ii++ ) {

                    Текст += "[";

                    for ( int jj = 0; jj < 31; jj++ ) {

                        N = НомерРасписанияЗаписиN( ( ( Integer ) План.Основной[ii][jj] ).intValue() );
                        Текст += "Р" + ( N + 1 );
                        Текст += ( jj == 30 ) ? "" : ",";
                    }

                    Текст += ( ii == 11 ) ? "]\n" : "],\n";
                }

                Текст += "];\n";

                // Сохраняем дополнительный план.
                Текст += "\n";
                Текст += "ДополнительныйПлан =\n";
                Текст += "[\n";

                for ( int ii = 0; ii < 12; ii++ ) {

                    Текст += "[";

                    for ( int jj = 0; jj < 31; jj++ ) {

                        N = НомерРасписанияЗаписиN( ( ( Integer ) План.Дополнительный[ii][jj] ).intValue() );
                        Текст += "Р" + ( N + 1 );
                        Текст += ( jj == 30 ) ? "" : ",";
                    }

                    Текст += ( ii == 11 ) ? "]\n" : "],\n";
                }

                Текст += "];\n";

                // Добавляем план.
                Текст += "\n";
                Текст += "ДобавитьПлан( \"" + План.Название + "\", ОсновнойПлан, ДополнительныйПлан );\n";
                Текст += "\n";
            }

        }

        switch ( Кодировка ) {

            case Settings.КОДИРОВКА_UTF8:

                Настройки.СтрокаВФайлUTF8( ИмяФайла, Текст );
                main.Логгер.info( "[Schedules.java]: ЭкспортРасписаний( \""
                        + ИмяФайла + "\", UTF-8 ); // Конец" );
                break;

            case Settings.КОДИРОВКА_CP1251:

                Настройки.СтрокаВФайлCP1251( ИмяФайла, Текст );
                main.Логгер.info( "[Schedules.java]: ЭкспортРасписаний( \""
                        + ИмяФайла + "\", CP-1251 ); // Конец" );
                break;
        }

    }


    public int НомерРасписанияЗаписиN( int Номер ) {

        Record Запись = ( Record ) Данные.Записи.elementAt( Номер );
        ScheduleClass Расписание;

        for ( int ii = 0; ii < Расписания.size(); ii++ ) {

            Расписание = ( ScheduleClass ) Расписания.elementAt(ii);

            if ( Запись.Имя.equals( Расписание.Название ) ) {

                return ii;
            }

        }

        return -1;
    }


    public void ИмпортРасписаний( String ИмяФайла, int Кодировка ) {

        switch ( Кодировка ) {

            case Settings.КОДИРОВКА_UTF8:

                main.Логгер.info( "[Schedules.java]: ИмпортРасписаний( \""
                        + ИмяФайла + "\", UTF-8 ); // Начало" );
                break;

            case Settings.КОДИРОВКА_CP1251:

                main.Логгер.info( "[Schedules.java]: ИмпортРасписаний( \""
                        + ИмяФайла + "\", CP-1251 ); // Начало" );
                break;
        }

        String Текст = null;

        Очистить();

        Record Запись = new Record();

        Запись.Верх = 0;
        Запись.Имя = "Корень";
        Запись.ТипЗаписи = ТИП_КОРЕНЬ;
        Запись.ТипЗвонка = ТИП_СВОБОДНЫЙ;
        Запись.Часы = 0;
        Запись.Минуты = 0;
        Запись.Секунды = 0;
        Запись.ПутьОсновн = "";
        Запись.ПутьПредвар = "";

        Данные.Добавить( Запись );

        switch ( Настройки.Основные.Кодировка ) {

            case Settings.КОДИРОВКА_UTF8:

                Текст = Настройки.ФайлUTF8ВСтроку( ИмяФайла );
                break;

            case Settings.КОДИРОВКА_CP1251:

                Текст = Utils.ФайлCP1251ВСтроку( ИмяФайла );
                break;
        }

        Parser Разборщик = new Parser( Мидлет, this, Настройки, Текст );

        Разборщик.Переменные.ДобавитьЭлемент( "ДобавитьГруппу", Parser.СВОЙСТВО_ФУНКЦИЯ, null );
        Разборщик.Переменные.ДобавитьЭлемент( "ДобавитьЗвонок", Parser.СВОЙСТВО_ФУНКЦИЯ, null );
        Разборщик.Переменные.ДобавитьЭлемент( "ДобавитьПлан", Parser.СВОЙСТВО_ФУНКЦИЯ, null );

        try {

            do {

                Разборщик.ПолучитьЭлемент();
                Разборщик.СложениеВычитание( false );

            } while ( Разборщик.Свойства.ТекущийЭлемент != Parser.ЭЛЕМЕНТ_КОНЕЦ );
//                } while ( ( Разборщик.Свойства.ТекущийЭлемент == Parser.ЭЛЕМЕНТ_ВЫВОД )
//                        || ( Разборщик.Свойства.ТекущийЭлемент != Parser.ЭЛЕМЕНТ_КОНЕЦ ) );

        } catch ( Exception e ) {}

        // Копирование записей в память.
        ScheduleClass Расписание;
        LessonClass Урок;
        BellClass Звонок;

        for ( int ii = 0; ii < Данные.Записи.size(); ii++ ) {

            Запись = ( Record ) Данные.Записи.elementAt(ii);

            if ( Запись.ТипЗаписи == ТИП_РАСПИСАНИЕ ) {

                // Добавляем одно расписание.
                Расписание = new ScheduleClass();
                Расписание.Название = Запись.Имя;
                Расписание.Уроки = new Vector();
                Расписание.цВыбранныйЭлемент = ii;
                цВыбранныйЭлемент = 0;
                Расписания.addElement( Расписание );
            }

            if ( Запись.ТипЗаписи == ТИП_ПЛАН ) {

                Parser.ОбразРезультата врм, Месяц, Число;
                Vector век, век2;
                врм = null;

                ОбразПлана План = new ОбразПлана();
                План.Название = Запись.Имя;

                Разборщик = new Parser( Мидлет, Настройки, Запись.ПутьОсновн );

                try {

                    Разборщик.ПолучитьЭлемент();
                    врм = Разборщик.СложениеВычитание( false );

                } catch ( Exception Ошибка ) {

                    main.Логгер.info( "[Schedules.java]: Ошибка при разборе массива основного плана." + Ошибка.getMessage() );
                }

                if ( врм == null ) continue;

                век = ( Vector ) врм.Значение.elementAt(0);
                врм = ( Parser.ОбразРезультата ) век.elementAt(0);
                век = ( Vector ) врм.Значение.elementAt(0);

                for ( int jj = 0; jj < 12; jj++ ) {

                    врм = ( Parser.ОбразРезультата ) век.elementAt(jj);
                    век2 = ( Vector ) врм.Значение.elementAt(0);
                    Месяц = ( Parser.ОбразРезультата ) век2.elementAt(0);
                    век2 = ( Vector ) Месяц.Значение.elementAt(0);

                    for ( int k = 0; k < 31; k++ ) {

                        Число = ( Parser.ОбразРезультата ) век2.elementAt(k);
                        План.Основной[jj][k] = ( Integer ) Число.Значение.elementAt(0);
                    }

                }

                Разборщик = new Parser( Мидлет, Настройки, Запись.ПутьПредвар );

                try {

                    Разборщик.ПолучитьЭлемент();
                    врм = Разборщик.СложениеВычитание( false );

                } catch ( Exception Ошибка ) {

                    main.Логгер.info( "[Schedules.java]: Ошибка при разборе массива дополнительного плана." + Ошибка.getMessage() );
                }

                if ( врм == null ) continue;

                век = ( Vector ) врм.Значение.elementAt(0);
                врм = ( Parser.ОбразРезультата ) век.elementAt(0);
                век = ( Vector ) врм.Значение.elementAt(0);

                for ( int jj = 0; jj < 12; jj++ ) {

                    врм = ( Parser.ОбразРезультата ) век.elementAt(jj);
                    век2 = ( Vector ) врм.Значение.elementAt(0);
                    Месяц = ( Parser.ОбразРезультата ) век2.elementAt(0);
                    век2 = ( Vector ) Месяц.Значение.elementAt(0);

                    for ( int k = 0; k < 31; k++ ) {

                        Число = ( Parser.ОбразРезультата ) век2.elementAt(k);
                        План.Дополнительный[jj][k] = ( Integer ) Число.Значение.elementAt(0);
                    }

                }

                СписокПланов.Элементы.addElement( План );
            }

        }

        for ( int ii = 0; ii < Расписания.size(); ii++ ) {

            Расписание = ( ScheduleClass ) Расписания.elementAt(ii);

            for ( int jj = 0; jj < Данные.Записи.size(); jj++ ) {

                Запись = ( Record ) Данные.Записи.elementAt(jj);

                if ( Запись.Верх == Расписание.цВыбранныйЭлемент ) {

                    Урок = new LessonClass();
                    Урок.Название = Запись.Имя;
                    Урок.Звонки = new Vector();
                    Урок.цВыбранныйЭлемент = jj;

                    Расписание.Уроки.addElement( Урок );
                }

            }

        }

        for ( int ii = 0; ii < Расписания.size(); ii++ ) {

            Расписание = ( ScheduleClass ) Расписания.elementAt(ii);

            for ( int jj = 0; jj < Расписание.Уроки.size(); jj++ ) {

                Урок = ( LessonClass ) Расписание.Уроки.elementAt(jj);

                for ( int k = 0; k < Данные.Записи.size(); k++ ) {

                    Запись = ( Record ) Данные.Записи.elementAt(k);

                    if ( Запись.Верх == Урок.цВыбранныйЭлемент ) {

                        Звонок = new BellClass();
                        Звонок.Тип = ( byte ) Запись.ТипЗвонка;
                        Звонок.Взведён = false;
                        Звонок.Включен = true;
                        Звонок.ПредЗнач = false;
                        Звонок.Путь = Запись.ПутьОсновн;
                        Звонок.Путь2 = Запись.ПутьПредвар;
                        Звонок.цЧасы = Запись.Часы;
                        Звонок.цМинуты = Запись.Минуты;
                        Звонок.цСекунды = Запись.Секунды;
                        Звонок.цВыбранныйЭлемент = k;

                        Урок.Звонки.addElement( Звонок );
                    }

                }

            }

        }

        switch ( Кодировка ) {

            case Settings.КОДИРОВКА_UTF8:

                Настройки.СтрокаВФайлUTF8( ИмяФайла, Текст );
                main.Логгер.info( "[Schedules.java]: ИмпортРасписаний( \""
                        + ИмяФайла + "\", UTF-8 ); // Конец" );
                break;

            case Settings.КОДИРОВКА_CP1251:

                Настройки.СтрокаВФайлCP1251( ИмяФайла, Текст );
                main.Логгер.info( "[Schedules.java]: ИмпортРасписаний( \""
                        + ИмяФайла + "\", CP-1251 ); // Конец" );
                break;
        }

    }


    public void Добавить( ScheduleClass Расписание ) {

        main.Логгер.info( "[Schedules.java]: Добавить( " + Расписание.Название + " )" );
        Расписания.addElement( Расписание );
    }


    public void Удалить( int цНомер ) {

        main.Логгер.info( "[Schedules.java]: Удалить( " + цНомер + " )" );
        Расписания.removeElementAt( цНомер );
    }


    public void Очистить() {

        main.Логгер.info( "[Schedules.java]: Очистить()" );

        ScheduleClass Расписание;
        LessonClass Урок;

        Данные.Записи.removeAllElements();
        СписокПланов.Элементы.removeAllElements();

        for ( Enumeration e = Расписания.elements(); e.hasMoreElements(); ) {

            Расписание = ( ScheduleClass ) e.nextElement();

            for ( Enumeration e1 = Расписание.Уроки.elements(); e1.hasMoreElements(); ) {

                Урок = ( LessonClass ) e1.nextElement();
                Урок.Звонки.removeAllElements();
            }

            Расписание.Уроки.removeAllElements();

        }

        Расписания.removeAllElements();
    }


    // Клонировать план.
    public void КлонироватьПлан( int Номер ) {

        main.Логгер.info( "[Schedules.java]: КлонироватьПлан( " + Номер + " )" );

        ОбразПлана План;
        ОбразПлана Клон = new ОбразПлана();

        План = ( ОбразПлана ) СписокПланов.Элементы.elementAt( Номер );
        Клон.Название = План.Название;

        for ( int ii = 0; ii < 12; ii++ ) {

            for ( int jj = 0; jj < 31; jj++ ) {

                Клон.Основной[ii][jj] = План.Основной[ii][jj];
                Клон.Дополнительный[ii][jj] = План.Дополнительный[ii][jj];
            }

        }

        СписокПланов.Элементы.addElement( Клон );
    }


    // Удалить план.
    public void УдалитьПлан( int Номер ) {

        main.Логгер.info( "[Schedules.java]: УдалитьПлан( " + Номер + " )" );
        СписокПланов.Элементы.removeElementAt( Номер );
    }


    // Добавить расписание.
    public int ДобавитьРасписание( String Название ) {

        main.Логгер.info( "[Schedules.java]: ДобавитьРасписание( " + Название + " )" );

        ScheduleClass Расписание;

        // Добавляем расписание.
        Расписание = new Schedules.ScheduleClass();
        Расписание.Название = Название;
        Расписание.Уроки = new Vector();
        Расписание.цВыбранныйЭлемент = 0; // инициализация для меню.

        Расписания.addElement( Расписание );

        // Дублируем запись во втором массиве данных.
        Record Запись;

        Запись = new Record();
        Запись.Верх = 0;
        Запись.Имя = Название;
        Запись.ТипЗаписи = ТИП_РАСПИСАНИЕ;
        Запись.ТипЗвонка = ТИП_СВОБОДНЫЙ;
        Запись.Часы = 0;
        Запись.Минуты = 0;
        Запись.Секунды = 0;
        Запись.ПутьОсновн = "";
        Запись.ПутьПредвар = "";

        Данные.Добавить( Запись );

        return Расписания.size() - 1;
    }


    // Изменить расписание.
    public void ИзменитьРасписание( int цРасписание, String Название ) {

        main.Логгер.info( "[Schedules.java]: ИзменитьРасписание( " + цРасписание + ", " + Название + " )" );

        ScheduleClass Расписание;

        // Изменяем название расписания.
        Расписание = ( ScheduleClass ) Расписания.elementAt( цРасписание );
        Расписание.Название = Название;
    }


    // Удалить расписание.
    public void УдалитьРасписание( int цРасписание ) {

        main.Логгер.info( "[Schedules.java]: УдалитьРасписание( " + цРасписание + " )" );

        ScheduleClass Расписание;
        LessonClass Урок;

        if ( цРасписание < 0 ) return;
        if ( цРасписание >= Расписания.size() ) return;

        Расписание = ( ScheduleClass ) Расписания.elementAt( цРасписание );

        // Освобождаем массив звонков у каждого урока.
        for ( Enumeration e = Расписание.Уроки.elements(); e.hasMoreElements(); ) {

            Урок = ( Schedules.LessonClass ) e.nextElement();
            Урок.Звонки.removeAllElements();

        }

        // Освобождаем массив уроков у расписания.
        Расписание.Уроки.removeAllElements();

        // Удаляем элемент из массива расписаний.
        Расписания.removeElementAt( цРасписание );
    }


    public int ДобавитьУрок( int цРасписание, String Название ){
        main.Логгер.info( "[Schedules.java]: ДобавитьУрок( " + цРасписание + ", " + Название + " )" );

        ScheduleClass Расписание;
        LessonClass Урок;

        Урок = new LessonClass();
        Урок.Название = Название;
        Урок.Звонки = new Vector();
        Урок.цВыбранныйЭлемент = 0; // инициализация для меню

        Расписание = ( ScheduleClass ) Расписания.elementAt( цРасписание );
        Расписание.Уроки.addElement( Урок );



        return Расписание.Уроки.size() - 1 ;
    }


    public void ИзменитьУрок( int цРасписание, int цУрок, String Название ) {

        main.Логгер.info( "[Schedules.java]: ИзменитьУрок( " + цРасписание + ", " + цУрок + ", " + Название + " )" );

        ScheduleClass Расписание;
        LessonClass Урок;

        Расписание = ( ScheduleClass ) Расписания.elementAt( цРасписание );
        Урок = ( LessonClass ) Расписание.Уроки.elementAt( цУрок );
        Урок.Название = Название;
    }


    public void УдалитьУрок( int цРасписание, int цУрок ) {

        main.Логгер.info( "[Schedules.java]: УдалитьУрок( " + цРасписание + ", " + цУрок + " )" );

        ScheduleClass Расписание;
        LessonClass Урок;

        Расписание = ( ScheduleClass ) Расписания.elementAt( цРасписание );

        if ( цУрок < 0 ) return;
        if ( цУрок >= Расписание.Уроки.size() ) return;

        Урок = ( LessonClass ) Расписание.Уроки.elementAt( цУрок );

        // Освобождаем массив звонков.
        Урок.Звонки.removeAllElements();
        Расписание.Уроки.removeElementAt( цУрок );
    }


    public int ДобавитьЗвонок( int цРасписание, int цУрок, int цЧасы, int цМинуты, byte Тип, String Путь, String Путь2 ) {

        main.Логгер.info( "[Schedules.java]: ДобавитьЗвонок(...)" );

        ScheduleClass Расписание;
        LessonClass Урок;
        BellClass Звонок;

        // Дабавляем звонок.
        Звонок = new BellClass();

        switch ( Тип ) {

            case 0:

                Звонок.Тип = ТИП_НАЧАЛО;
                break;

            case 1:

                Звонок.Тип = ТИП_КОНЕЦ;
                break;

            case 2:

                Звонок.Тип = ТИП_СВОБОДНЫЙ;
                break;

        }

        Звонок.Взведён = false;
        Звонок.Включен = true;
        Звонок.ПредЗнач = false;
        Звонок.Путь = Путь;
        Звонок.Путь2 = Путь2;
        Звонок.цЧасы = цЧасы;
        Звонок.цМинуты = цМинуты;
        Звонок.цСекунды = 0;
        Звонок.цВыбранныйЭлемент = 0; // инициализация для меню

        Расписание = ( ScheduleClass ) Расписания.elementAt( цРасписание );
        Урок = ( LessonClass ) Расписание.Уроки.elementAt( цУрок );
        Урок.Звонки.addElement( Звонок );

        return Урок.Звонки.size() - 1;
    }


    public void ИзменитьЗвонок( int цРасписание, int цУрок, int цЗвонок, int цЧасы, int цМинуты, byte Тип, String Путь, String Путь2 ) {

        main.Логгер.info( "[Schedules.java]: ИзменитьЗвонок(...)" );

        ScheduleClass Расписание;
        LessonClass Урок;
        BellClass Звонок;

        Расписание = ( ScheduleClass ) Расписания.elementAt( цРасписание );
        Урок = ( LessonClass ) Расписание.Уроки.elementAt( цУрок );
        Звонок = ( BellClass ) Урок.Звонки.elementAt( цЗвонок );

        Звонок.цЧасы = цЧасы;
        Звонок.цМинуты = цМинуты;

        switch ( Тип ) {

            case 0:
                Звонок.Тип = ТИП_НАЧАЛО;
                break;

            case 1:

                Звонок.Тип = ТИП_КОНЕЦ;
                break;

            case 2:
                Звонок.Тип = ТИП_СВОБОДНЫЙ;
                break;
        }

        Звонок.Путь = Путь;
        Звонок.Путь2 = Путь2;

    }


    public void ИзменитьМелодию( int цРасписание, int цУрок, int цЗвонок, int Номер, String Путь ) {

        main.Логгер.info( "[Schedules.java]: ИзменитьМелодию(...)" );

        ScheduleClass Расписание;
        LessonClass Урок;
        BellClass Звонок;

        Расписание = ( ScheduleClass ) Расписания.elementAt( цРасписание );
        Урок = ( LessonClass ) Расписание.Уроки.elementAt( цУрок );
        Звонок = ( BellClass ) Урок.Звонки.elementAt( цЗвонок );

        switch ( Номер ) {

            case 0:
                Звонок.Путь = Путь;
                break;

            case 1:
                Звонок.Путь2 = Путь;
                break;
        }

    }


    public void УдалитьЗвонок( int цРасписание, int цУрок, int цЗвонок ) {

        main.Логгер.info( "[Schedules.java]: УдалитьЗвонок(...)" );

        ScheduleClass Расписание;
        LessonClass Урок;

        Расписание = ( ScheduleClass ) Расписания.elementAt( цРасписание );
        Урок = ( LessonClass ) Расписание.Уроки.elementAt( цУрок );

        if ( цУрок < 0 ) return;
        if ( цУрок >= Урок.Звонки.size() ) return;

        Урок.Звонки.removeElementAt( цЗвонок );

    }

}
