package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";
	private static final String NOT_CONSECUTIVE_NUMBER= "売上ファイルが連番になっていません";
	private static final String TOTAL_AMOUNT_EXEEDED= "合計⾦額が10桁を超えました";
	private static final String CODEL_IS_INVALID= "の⽀店コードが不正です";
	private static final String FAIL_INVALID_FORMAT= "売上ファイルのフォーマットが不正です";


	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {

		if (args.length != 1) {
		    //コマンドライン引数が1つ設定されていなかった場合は、
		    //エラーメッセージをコンソールに表⽰します。
			System.out.println(UNKNOWN_ERROR);
			return;
		}
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)

		// ファイルのパスの中にある情報を取得するためにlistFilesという方法を使ってFile[] filesの中に入れている。
		File[] files = new File(args[0]).listFiles();
		//型（形）変数（名前）＝（入れる行為）.方法

		//List宣言から行う
		List<File> rcdFiles = new ArrayList<>();

		for(int i = 0; i < files.length ; i++) {
			if(files[i].isFile() && files[i].getName().matches("^[0-9]{8}\\.rcd$")) {
			    //対象がファイルであり、「数字8桁.rcd」なのか判定

				rcdFiles.add(files[i]);
				//条件に当てはまったものだけリスト化
			}

		}

		//ファイルが連番になっていなかった場合のエラー処理
		 //繰り返し回数は売上ファイルのリストの数よりも1つ⼩さい数

		Collections.sort(rcdFiles);

		for(int i = 0; i < rcdFiles.size() -1; i++) {
							//売上ファイルのリスト

			int former = Integer.parseInt(rcdFiles.get(i).getName().substring(0, 8));
											//ファイル名
			int latter = Integer.parseInt(rcdFiles.get(i + 1).getName().substring(0, 8));
											//次のファイル名

		      //⽐較する2つのファイル名の先頭から数字の8⽂字を切り出し、int型に変換
			if((latter - former) != 1) {
				//2つのファイル名の数字を⽐較して、差が1ではなかったら、
				//エラーメッセージをコンソールに表⽰
				System.out.println(NOT_CONSECUTIVE_NUMBER);
				return;
			}
		}

		for(int i = 0; i < rcdFiles.size(); i++) {

			//売上ファイルの1行目には支店コード、2行目には売上金額が入っています。
			BufferedReader br = null;
			try {

				File file = new File(args[0], rcdFiles.get(i).getName());

				FileReader fr = new FileReader(file);
				br = new BufferedReader(fr);

				//リードラインメゾットでテキストを一行ずつ読み込ませる
				//while＝指定された条件が真である限り、コードブロックを繰り返し実行するための制御構文

				// 1.リストを作る
				List<String> fileContents = new ArrayList<String>();

				String line;
				while((line = br.readLine()) != null) {

				    //作ったリストに支店コードと金額をいれる
					fileContents.add(line);
			     }

				//エラー処理 売上ファイルの中⾝が3⾏以上ある場合
				if(fileContents.size() != 2) {
					//売上ファイルの中⾝を⼊れたリスト

				    //売上ファイルの⾏数が2⾏ではなかった場合は、エラーメッセージをコンソールに表⽰します。
					System.out.println(FAIL_INVALID_FORMAT);
					return;
				}

				//エラー処理 ⽀店に該当がなかった場合
				if (!branchNames.containsKey(fileContents.get(0))) {
					//⽀店コードを⼊れたMap	(支店コード)

				    //⽀店情報を保持しているMapに売上ファイルの⽀店コードが存在しなかった場合は、
				    //エラーメッセージをコンソールに表⽰します。
					System.out.println(rcdFiles.get(i).getName() + CODEL_IS_INVALID);
					return;
				}

					//売上金額					//正規表現式（数字）
				if(!fileContents.get(1).matches("^[0-9]*$")) {
				    //売上⾦額が数字ではなかった場合は、
				    //エラーメッセージをコンソールに表⽰します。
					System.out.println(UNKNOWN_ERROR);
					return;

				//エラー処理 ファイルの中身が二桁か確認→売上ファイルの支店コードの確認→金額が数字か確認
				}

				//売上ファイルから読み込んだ売上金額をMapに加算するため、型の変換をする
				long fileSale = Long.parseLong(fileContents.get(1));

				//ファイルコンテンツ（支店コード）をブランチコードに入れた
				String branchCode = fileContents.get(0);



				//saleAmount(売上金額)の中に、バリュー（0L）＋売上ファイルから読み込んだ売上金（fileSale）を入れる
				Long saleAmount = branchSales.get(branchCode) + fileSale;
								//売上金額を入れたMap.get(支店コード)

				if(saleAmount >= 10000000000L){
					//売上⾦額が11桁以上の場合、エラーメッセージをコンソールに表⽰します。
					System.out.println(TOTAL_AMOUNT_EXEEDED);
					return;
				}

				//ブランチセールスの中に、支店コードと、売上金額が入っているsaleAmountが入っている
				branchSales.put(branchCode, saleAmount);

			}
			catch(IOException e) {
				System.out.println(UNKNOWN_ERROR);
				return;
			} finally {
				// ファイルを開いている場合
				if(br != null) {
					try {
						// ファイルを閉じる
						br.close();
					} catch(IOException e) {
						System.out.println(UNKNOWN_ERROR);
						return;
					}
				}
			}

		}

		// 支店別集計ファイル書き込み処理
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}
	}

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {

			File file = new File(path, fileName);
								//(ファイルのパス, ファイルの名前)
			if(!file.exists()) {
			    //⽀店定義ファイルが存在しない場合、コンソールにエラーメッセージを表⽰します。
				System.out.println(FILE_NOT_EXIST);
				return false;
				//エラーメッセージを表示し、処理を終了する
			}

			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// (処理内容1-2)

			    //items[0] には⽀店コード、items[1] には⽀店名が格納されます。
			    String[] items = line.split(",");

			    if((items.length != 2) || (!fileName.matches("^[0-9]{8}\\.rcd$"))){
			    	//配列					//ファイル名		//正規表現構文
					//⽀店定義ファイルの仕様が満たされていない場合、
				    //エラーメッセージをコンソールに表⽰します。
			    	System.out.println(FILE_INVALID_FORMAT);
			    	return false;
			    }

			    branchNames.put(items[0], items[1]);
	            branchSales.put(items[0], 0L);
			}

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					br.close();// ファイルを閉じる
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		// (処理内容3-1)
		BufferedWriter bw = null;

		try {
			//例外が発生する可能性のある処理 支店別集計ファイル
			File file = new File(path, fileName);

			//プログラムから文字列を受け取ってFileWriterオブジェクトに文字列を渡すBufferedWriterオブジェクトを生成
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			//For文を使ってMAPからkey一覧を取得、keyの数だけ繰り返す
			for (String key : branchSales.keySet()) {

				branchSales.get(key);//mapの中のkeyに紐づいてるバリュー（売上金額）を取ってきている
				branchNames.get(key);//mapの中のkeyに紐づいてるバリュー（支店名）を取ってきている

				//順番を指定して書き込む
				bw.write(key  + "," + branchNames.get(key) + "," + branchSales.get(key));

				bw.newLine();//改行する
			}

		} catch(IOException e) {
			System.out.println(e);
			return false;

		} finally {
	    	 //必ず実行される処理を書く
					// ファイルを開いている場合
			if(bw != null) {
				try { bw.close();
							// ファイルを閉じる

				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;

				}
			}
		}
		return true;
	}
}
