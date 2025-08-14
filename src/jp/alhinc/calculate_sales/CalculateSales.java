package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}



		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)
		// ファイルのパスの中にある情報を取得するためにlistFilesという方法を使ってFile[] filesの中に入れてる
		//型（ハコの形）変数（ハコの名前）＝（入れる行為）何を？、（なんの方法で？）
		File[] files = new File("C:\\Users\\trainee1195\\Documents\\売上集計課題").listFiles();

		//List宣言から行う
		List<File> rcdFiles = new ArrayList<>();

		for(int i = 0; i < files.length ; i++) {
			//files[i].getName() でファイル名が取得できます。

			//[i]に１を入れれば１になるし2を入れれば２になる なんにでもなれるってこと？
			// String型(ハコ形）のfileName（ハコ名前）をiの順番でGETする（入手）
			String fileName = files[i].getName();

			//もしその箱の名前（fileName)
			if(fileName.matches("^[0-9]{8}\\.rcd$")) {
				//0から1の数字八桁とrcdで終わる

				rcdFiles.add(files[i]);
				//条件に当てはまったものだけリスト化↑
			}

		}
		//FOR文は繰り返す　＋１２３　ー３２１
		//FOR文内に同じ変数は無理（int i)
		//プログラミングにいまからrcdリストを読み込ませる
	//	intiに0をいれた、rcdFilesの大きさよりもiが小さい　小さい分繰り返す
		for(int i = 0; i < rcdFiles.size(); i++) {

			//売上ファイルの1行目には支店コード、2行目には売上金額が入っています。
			BufferedReader br = null;
			try {
				//rcdfiles.get(i)←これで売上ファイルのi番目をとれる
				File file = new File("C:\\Users\\trainee1195\\Documents\\売上集計課題", rcdFiles.get(i).getName());

				//こちらのファイル、一行ずつ読み込ませます
				//読み込ませるための処理↓

				FileReader fr = new FileReader(file); //
				br = new BufferedReader(fr);
				//上でハコの用意をした
				//リードラインメゾットでテキストを一行ずつ読み込ませる
				//while＝指定された条件が真である限り、コードブロックを繰り返し実行するための制御構文

				// 1.リストを作る
				  List<String> fileContents = new ArrayList<String>();

				String line;
				while((line = br.readLine()) != null) {

				    //作ったリストにコードと金額をいれる
					//lineファイルを入れることで、中にある数字たちがリスト化されます
					fileContents.add(line);
			     }
				    //
				//今リスト作ってrcdファイルの中身を追加したところ

				//売上ファイルから読み込んだ売上金額をMapに加算したいから型の変換をする
					//読み込んだ情報は一律Strong扱い
					//だけど、売上金額はLong扱いしたいからMapに追加するための型変換をする

				//金額を0に設定したからi＝0

 			long fileSale = Long.parseLong(fileContents.get(1));
 			//型変換させた

 			//
			Long saleAmount = branchSales.get(0) + fileSale;



			} catch(IOException e) {
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
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)
				//split を使って「,」(カンマ)で分割すると、
			    //items[0] には⽀店コード、items[1] には⽀店名が格納されます。
			    String[] items = line.split(",");


			    branchNames.put(items[0], items[1]);
	            branchSales.put(items[0], 0L);
			    System.out.println(line);

			}


		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					// ファイルを閉じる
					br.close();
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
		// ※ここに書き込み処理を作成してください。(処理内容3-1)

		return true;
	}

}
