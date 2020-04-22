import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.*

fun main() {
    val c: Connection = DriverManager.getConnection(
        //"109.187.18.186:3306",
        "jdbc:mysql://localhost:3306/BD_Students?serverTimezone=UTC",
        "root",//root
        "Fanzil!02"
    )
    val s: Statement = c.createStatement()

    val dt1 = "drop table if exists `student`"
    val dt2 = "drop table if exists `subject`"
    val dt3 = "drop table if exists `mark`"
    s.execute(dt3)
    s.execute(dt1) //выполнить
    s.execute(dt2)

    val ct1: String = "create table if not exists `student` (" +
            "id int auto_increment primary key, " +
            "name varchar(30) not null, " +
            "surname varchar(30) not null, " +
            "patronymic varchar(30), " +
            "group_num varchar(6) not null, " +
            "birth date not null, " +
            "start_date year not null" +
            ");"

    val ct2: String = "create table if not exists `subject` ("+
            "id int auto_increment primary key, "+
            "name varchar(30) not null, "+
            "semester int not null, "+
            "control enum('Зачет', 'Диф.зачет', 'Экзамен') not null, "+
            "hours int not null "+
            ");"

    val ct3: String = "create table if not exists `mark` (" +
            "id int auto_increment primary key, " +
            "stud_id int not null, " +
            "subj_id int not null, " +
            "mark int not null, " +
            "constraint `stud` foreign key (`stud_id`) references `student` (`id`), " +
            "constraint `subj` foreign key (`subj_id`) references `subject` (`id`) " +
            ");"
    s.execute(ct1)
    s.execute(ct2)
    s.execute(ct3)

    val files: List<String> = listOf("student.csv", "subject.csv", "mark.csv")
    for (f: String in files) {
        val br = BufferedReader(
            InputStreamReader(
                FileInputStream(f)
            )
        )
        val tb1 = f.split(".")[0]
        var first = true
        var cols = listOf<String>()
        while (br.ready()) {
            val l = br.readLine()
            if(l==""){
                continue
            }
            if (first && l != null) {
                first = false
                cols = l.split(";")
                continue
            }
            if (l != null) {
                val vals = l.split(";")
                var q = "INSERT INTO `$tb1` ("
                for (i in 0 until cols.size) {
                    q += "`${cols[i]}`"
                    if (i < cols.size - 1) q += ", "
                }
                q += ") VALUES ("
                for (i in 0 until vals.size) {
                    q += "'${vals[i]}'"
                    if (i < vals.size - 1) q += ", "
                }
                q += ");"
                s.execute(q)
            } else break
        }
    }

    //Список студенов определенной группы
    /* val sc = Scanner(System.`in`)
val group_num = sc.next()
val sq1 = "SELECT name,surname,patronymic " +
        "FROM `student` " +
        "WHERE group_num='$group_num' " +
        "ORDER BY surname, name, patronymic;"
val result1 = s.executeQuery(sq1)
while (result1.next()) {
    print(result1.getString("surname"))
    print(" ")
    print(result1.getString("name"))
    print(" ")
    print(result1.getString("patronymic"))
    println()
}*/

    //Вывод среднего балла судента
    /*val sq2 = "SELECT student.name, student.surname, student.patronymic, AVG(mark) "+
            "FROM `student` "+
            "INNER JOIN `mark` "+
            "ON student.id=mark.stud_id "+
            "GROUP BY mark.stud_id;"
    val result2 = s.executeQuery(sq2)
    while (result2.next()) {
        print(result2.getString("surname"))
        print(" ")
        print(result2.getString("name"))
        print(" ")
        print(result2.getString("patronymic"))
        print(" ")
        print(result2.getString("AVG(mark)"))
        println()
    }*/





    //Вывод стипендии студентов
    val sq3 = "Select *, IF(Min_mark<4,'0p',if(Min_mark=4,'2000p','3500p')) as Stip From "+
            "(SELECT Id, FIO, group_num, MIN(Progress) as Min_mark FROM "+
            "(select Sid as Id, CONCAT(surname,' ', N,'.', P, '.') as FIO, group_num, Progress, Lastsem, subj_sem "+
            "from "+
            "(Select student.id as Sid, student.surname, substring(student.name, 1, 1) as N, "+
            "substring(student.patronymic, 1, 1) as P,student.group_num, "+
            "2*(year(now())-student.start_date)-if(month(now())=1,2,if(month(now())>=2 and month(now())<=6,1,0)) "+
            "as Lastsem from student) "+
            "as Studsess "+


            "inner join "+
            "( select mark.subj_id, mark.stud_id, "+
            "if(mark.mark<56,2,if(mark.mark<71,3,if(mark.mark<86,4,5))) as Progress, subject.semester as subj_sem "+
            "from mark "+
            "inner join subject "+
            "on subject.id = mark.subj_id) as Stud_marks "+
            "on Sid=Stud_marks.stud_id) as Final "+

            "WHERE Lastsem=subj_sem "+

            "GROUP BY Id) as Fin "+
            //"WHERE Min_mark>=4 "+
            "GROUP BY Id"

    print(sq3)


    val result3 = s.executeQuery(sq3)
    while (result3.next()) {
        print(result3.getString("Id"))
        print(" ")
        print(result3.getString("FIO"))
        print(" ")
        print(result3.getString("group_num"))
        print(", ")
        print("Мин.оценка: ")
        print(result3.getString("Min_mark"))
        print(", ")
        print("Стипендия: ")
        print(result3.getString("Stip"))
        println()
    }

}