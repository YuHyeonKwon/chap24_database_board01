package com.javalab.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Borad01 {

	// 1. oracle 드라이버 이름 문자열 상수
	public static final String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
	
	// 2. oracle 데이터베이스 접속 경로(url) 문자열 상수
	public static final String DB_URL = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
	
	// 3. 데이터베이스 전송 객체
	public static Connection con = null;
	
	// 4. query 실행 객체
	public static PreparedStatement pstmt = null;
	
	// 5. select 결과 저장 객체
	public static ResultSet rs = null;
	
	// 6. oracle 계정(ID / PWD)
	public static String oracleId = "board";
	public static String oraclePwd = "1234";
	
	public static void main(String[] args) {
		// 1. 디비 접속 메소드 호출
		connectDB();
		
//		// 2. 게시물 목록 조회
//		getBoardList();
//		
//		// 3. 새글 등록
//		// 새글 등록이 완료되었으면 주석처리 한후에 답글등록으로 이동
//		insertNewBoard();
//		
//		// 4. 답글등록
		// 어떤 게시물에 답글을 달지 부모 게시글의 정보를 전달해야함.
//		int replyGroup = 31;	// 부모글의 그룹번호
//		int replyOrder = 1;		// 부모글의 그룹내순서
//		int replyIndent = 1; 	// 부모글의 들여쓰기
//		
//		insertReply(replyGroup, replyOrder, replyIndent);
//		
//		// 5. 게시물 목록 조회(반드시 1번~5번까지)
//		int startNo = 1;
//		int length = 5;
//		getBoardListTopn(startNo, length);
//		
//		// 6. 중간에 특정 부분 조회(5번 ~9번까지)
//		int startNo = 5;
//		int length = 9;
//		getBoardListPart(startNo, length);
//		
//		// 7. 게시물 조회수 증가
//		int bno = 21;	// 조회수를 증가시킬  게시물 번호
//		updateCount(bno);
//		
//		// 8. 수정
//		// 5번 게시물의 제목을 "다섯번째 글"로 수정하세요.
//		int bno = 31;
//		String newTitle = "새로운 다섯번째 글";
//		updateTitle(bno, newTitle);
//		
//		// 9. user01님이 작성한 게시물을 모두 삭제하세요.
		String userName = "user01";
		deleteBoard(userName);
//		
		// 10. 자원반납
		closeResource();
		
	} // main e

	// 1. 디비 접속 메소드 호출
	private static void connectDB() {
		try {
			// 1. 드라이버 로딩
			Class.forName(DRIVER_NAME);
			System.out.println("1. 드라이버 로드 성공!");
			
			// 2. 데이터베이스 커넥션(연결)
			con = DriverManager.getConnection(DB_URL, oracleId, oraclePwd);
			System.out.println("2. 커넥션 객체 생성 성공");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로드 ERR!: " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("SQL ERR!: " + e.getMessage());
		}
	} // end method

	// 2. 게시물 목록 조회
	private static void getBoardList() {
		try {
			// 3. preparedStatement 객체를 통해서 쿼리하기 위한 SQL 뭐리문장 만들기(삽입,수정,삭제,조회)
			String sql = "select b.bno, b.title, b.content, b.member_id, b.count, to_char(b.created_date, 'yyyy-mm-dd') as created_date,";
			sql += " b.reply_group, b.reply_order, b.reply_indent,";
			sql += " m.name, m.pwd, m.email, m.handphone, m.admin, m.address";
			sql += " from tbl_member m left outer join tbl_board b on m.member_id = b.member_id";
			sql += " order by b.bno asc, m.member_id desc";
			
			// 4. 커넥션 객치를 통해서 데이터베이스에 쿼리(SQL)를 실행해주는 preparedStatement
			pstmt = con.prepareStatement(sql);
			System.out.println("3. pstmt 객체 생성 성공");
			
			// 5. Statement 객체의 executeQuery() 메소드를 통해서 쿼리 실행
			rs = pstmt.executeQuery();
			System.out.println("게시물 전체조회 성공");
			
			while (rs.next()) {
				System.out.println(rs.getInt("bno") + "\t" +
								rs.getString("title") + "\t" +
								rs.getString("content") + "\t" +
								rs.getString("member_id") + "\t" +
								rs.getInt("count") + "\t" +
								rs.getDate("created_date") + "\t" +
								rs.getInt("reply_group") + "\t" +
								rs.getInt("reply_order") + "\t" +
								rs.getInt("reply_indent") + "\t" +
								rs.getString("name") + "\t" +
								rs.getString("pwd") + "\t" +
								rs.getString("email") + "\t" +
								rs.getString("handphone") + "\t" +
								rs.getInt("admin") + "\t" +
								rs.getString("address"));
			}
		} catch (SQLException e) {
			System.out.println("SQL ERR!: " + e.getMessage());
		} finally {
			closeResource();
		}
	} // end method

	// 3. 새글 등록
	// 새글 등록이 완료되었으면 주석처리 한후에 답글등록으로 이동
	private static void insertNewBoard() {
		try {
			String title = "새로운글";
			String content = "새로운글내용";
			String member_id = "user01";
			int count = 0;
			int replyOrder = 0;
			int replyIndent = 0;
			
			String sql = "insert into tbl_board (bno, title, content, member_id, count, created_date,";
			sql += " reply_group, reply_order, reply_indent)";
			sql += " values(seq_bno.nextval, ?, ?, ?, ?, to_date(sysdate, 'yyyy-mm-dd'),";
			sql += " seq_bno.currval, ?, ?)";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			pstmt.setString(3, member_id);
			pstmt.setInt(4, count);
			pstmt.setInt(5, replyOrder);
			pstmt.setInt(6, replyIndent);
			
			int result = pstmt.executeUpdate();
			if (result > 0) {
				System.out.println("저장 성공!");
			} else {
				System.out.println("저장 실패!");
			}
		} catch (SQLException e) {
			System.out.println("SQL ERR!: " + e.getMessage());
		} finally {
			closeResource();
		}
	} // end method

	// 4. 답글등록
	// 어떤 게시물에 답글을 달지 부모 게시글의 정보를 전달해야함.
	private static void insertReply(int replyGroup, int replyOrder, int replyIndent) {
		try {
			String title = "[답글]";
			String content = "[답글] 내용";
			String member_id = "user02";
			int count = 0;
			
			String sql = "insert into tbl_board (bno, title, content, member_id, count, created_date,";
			sql += " reply_group, reply_order, reply_indent)";
			sql += " values(seq_bno.nextval, ?, ?, ?, ?, to_date(sysdate, 'yyyy-mm-dd'),";
			sql += " ?, ?, ?)";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			pstmt.setString(3, member_id);
			pstmt.setInt(4, count);
			pstmt.setInt(5, replyGroup);
			pstmt.setInt(6, replyOrder);
			pstmt.setInt(7, replyIndent);
			
			int result = pstmt.executeUpdate();
			if (result > 0) {
				System.out.println("저장 성공!");
			} else {
				System.out.println("저장 실패!");
			}
		} catch (SQLException e) {
			System.out.println("SQL ERR!: " + e.getMessage());
		} finally {
			closeResource();
		}
	} // end method

	// 5. 게시물 목록 조회(반드시 1번~5번까지)
	private static void getBoardListTopn(int startNo, int length) {
		String sql = " ";
		try {
			sql = "select b.bno, b.title, b.content, b.member_id, b.count, to_char(b.created_date, 'yyyy-mm-dd') as created_date,";
			sql += " b.reply_group, b.reply_order, b.reply_indent";
			sql += " from(";
			sql += " select rownum rnum, a.*";
			sql += " from(";
			sql += " select b.*";
			sql += " from tbl_board b";
			sql += " order by b.reply_group desc, reply_order asc";
			sql += " )a";
			sql += " )b";
			sql += " where rnum between ? and ?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, startNo);
			pstmt.setInt(2, length);
			
			rs = pstmt.executeQuery();
			System.out.println();
			
			while (rs.next()) {
				String strInd = " ";
				int result = rs.getInt("reply_indent");
				if (result > 0) {
					for (int i =0; i<result; i++) {
						strInd += " ";
					}
				}
				System.out.println(strInd + rs.getInt("bno") + "\t" +
						rs.getString("title") + "\t" +
						rs.getString("content") + "\t" +
						rs.getString("member_id") + "\t" +
						rs.getInt("count") + "\t" +
						rs.getDate("created_date") + "\t" +
						rs.getInt("reply_group") + "\t" +
						rs.getInt("reply_order") + "\t" +
						rs.getInt("reply_indent"));			
			}
		} catch (SQLException e) {
			System.out.println("SQL ERR!: " + e.getMessage());
		} finally {
			closeResource();
		}
	} // end method

	// 6. 중간에 특정 부분 조회(5번 ~9번까지)
	private static void getBoardListPart(int starNo, int length) {
		String sql = " ";
		try {
			sql = "select b.bno, b.title, b.content, b.member_id, b.count, to_char(b.created_date, 'yyyy-mm-dd') as created_date,";
			sql += " b.reply_group, b.reply_order, b.reply_indent";
			sql += " from(";
			sql += " select rownum rnum, a.*";
			sql += " from(";
			sql += " select b.*";
			sql += " from tbl_board b";
			sql += " order by b.reply_group desc, reply_order asc";
			sql += " )a";
			sql += " )b";
			sql += " where rnum between ? and ?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, starNo);
			pstmt.setInt(2, length);
			
			rs = pstmt.executeQuery();
			System.out.println();
			
			while (rs.next()) {
				String strInd = " ";
				int result = rs.getInt("reply_indent");
				if (result > 0) {
					for (int i =0; i<result; i++) {
						strInd += " ";
					}
				}
				System.out.println(strInd + rs.getInt("bno") + "\t" +
						rs.getString("title") + "\t" +
						rs.getString("content") + "\t" +
						rs.getString("member_id") + "\t" +
						rs.getInt("count") + "\t" +
						rs.getDate("created_date") + "\t" +
						rs.getInt("reply_group") + "\t" +
						rs.getInt("reply_order") + "\t" +
						rs.getInt("reply_indent"));			
			}
		} catch (SQLException e) {
			System.out.println("SQL ERR!: " + e.getMessage());
		} finally {
			closeResource();
		}		
	} // end method

	// 7. 게시물 조회수 증가
	private static void updateCount(int bno) {
		try {
			String sql = "update tbl_board";
			sql += " set count = count + count + 1";
			sql += " where bno = ?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, bno);
			
			int result = pstmt.executeUpdate();
			
			if (result > 0) {
				System.out.println("조회수 증가 성공!");
			} else {
				System.out.println("조회수 증가 실패!");
			}
		} catch (SQLException e) {
			System.out.println("SQL ERR!: " + e.getMessage());
		} finally {
			closeResource();
		}
	} // end method

	// 8. 수정
	// 5번 게시물의 제목을 "다섯번째 글"로 수정하세요.
	private static void updateTitle(int bno, String newTitle) {
		try {
			String sql = "update tbl_board";
			sql += " set title = ?";
			sql += " where bno = ?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, newTitle);
			pstmt.setInt(2, bno);
			
			int result = pstmt.executeUpdate();
			
			if (result >0) {
				System.out.println("타이틀 수정 성공!");
			} else {
				System.out.println("타이틀 수정 실패!");
			}
		} catch (SQLException e) {
			System.out.println("SQL ERR!: " + e.getMessage());
		} finally {
			closeResource();
		}
	} // end method

	// 9. user01님이 작성한 게시물을 모두 삭제하세요.
	private static void deleteBoard(String userName) {
		try {
			String sql = "delete from tbl_board";
			sql += " where member_id = ?"; 
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userName);
			
			int result = pstmt.executeUpdate();
			if (result > 0) {
				System.out.println("삭제 성공!");
			} else {
				System.out.println("삭제 실패!");
			}
		} catch (SQLException e) {
			System.out.println("SQL ERR!: " + e.getMessage());
		} finally {
			closeResource();
		}
	} // end method

	// 10. 자원반납
	private static void closeResource() {
		try {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			System.out.println("SQL ERR!: " + e.getMessage());
		}
	} // end method

} // class e
