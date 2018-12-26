package com.outsider.maxEnt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * ��ʵ��һ��������ͨ���ݵ������ģ�ͣ����������ݡ�
 * ���ܶ�������ģ������ȡ������������
 * ֻ��ʹ����ɢ������
 * �������Ȼ���ʺ϶���࣬���Զ����Ҳû���⡣
 * �������û��ȡ���������Ǹ������㶼��double���������floatҪ�ߡ�
 * �Ż���������IIS
 * �������������Ĵ洢������ֻ�Ǽ򵥵���list��map������������ϴ����������϶��ȽϷ��ڴ棬����DoubleArrayTire�洢���������ȽϺ��ʡ���˫�����ֵ�����
 * ��Ҫע�⣬������ݼ�����������ͬά�ȵ�������ȡֵ��Χ��һ���ģ�����ͼƬ�в�ͬά�ȵ�����ֵ�������������ҪΪÿ��ά�ȼ���һ��ά�ȱ�ʶ��
 * ��Ȼ��ͬά�ȵ�ȡֵ��Χһ���Ķ��ά�Ƚ��˻�Ϊ����ά��
 * @author outsider
 */
public class MaxEnt {
	//x������y��ǩ����Ϊint����
	private Map<String, Integer> x2id;
	//intId��Ϊ�����ַ�
	private String[] id2x;
	private Map<String, Integer> y2id;
	private String[] id2y;
	//��������
	public List<FeatureFunction> funcs;
	//��������id��int��ӳ��
	private Map<String, Integer> funcId2Int; 
	/**
	 * ѵ���ӿ�
	 * @param table ��ά�����ݸ�ʽ
	 * @param xColumnIndexes ʹ����Щ����Ϊ������
	 * @param yColumnIndex ��ǩ������
	 * @param maxIter ����������
	 * @param epsilon ������������
	 */
	public void train(Table table, int[] xColumnIndexes, int yColumnIndex, int maxIter, double epsilon) {
		List<String[]> xs = new ArrayList<>();
		String[] y = new String[table.getRowNum()];
		for(int i = 0; i < table.getRowNum(); i++) {
			String[] x = new String[xColumnIndexes.length];
			for(int j = 0; j < x.length; j++) {
				x[j] = table.get(i, xColumnIndexes[j]);
			}
			xs.add(x);
			y[i] = table.get(i, yColumnIndex);
		}
		//�Ա�ǩ������������
		encode(xs, y);
		//������������
		int M = generateFeatureFunction(xs, y);
		//�����Mȡ������ά��
		M = table.getColumnNum();
		//���չ�ʽMӦ�������������������ֵĴ���
		System.out.println("M:"+M);
		System.out.println("epsilon:"+epsilon);
		System.out.println("maxIter:"+maxIter);
		System.out.println("Amount of FeatureFunction:"+id2x.length);
		System.out.println("Amout of Label:"+id2y.length);
		System.out.println("train data.shape:"+xs.size()+","+xs.get(0).length);
		//�Ż�
		//IIS(maxIter, epsilon, table, xColumnIndex, yColumnIndex);
		IIS(maxIter, epsilon, xs , y, M);
	}
	
	/**
	 * ��ȡy��ǩ����
	 * ����Ԥ����
	 * @param index
	 * @return
	 */
	public String yName(int index) {
		return id2y[index];
	}
	
	/**
	 * ����ɢ��x������y��ǩ����int���룬���㴦��
	 * @param table
	 * @param xColumnIndexes
	 * @param yColumnIndex
	 */
	public void encode(List<String[]> xs, String[] y) {
		Set<String> xSet = new HashSet<>();
		Set<String> ySet = new HashSet<>();
		for(String[] xArr : xs) {
			for(String x : xArr) {
				xSet.add(x);
			}
		}
		for(String yy : y) {
			ySet.add(yy);
		}
		id2x = new String[xSet.size()];
		id2y = new String[ySet.size()];
		x2id = new HashMap<>();
		y2id = new HashMap<>();
		int count = 0;
		for(String s : xSet) {
			id2x[count] = s;
			x2id.put(s, count);
			count++;
		}
		count = 0;
		for(String s : ySet) {
			id2y[count] = s;
			y2id.put(s, count);
			count++;
		}
	}
	
	/**
	 * ������������
	 * @param table
	 * @param xColumnIndexes
	 * @param yColumnIndex
	 * @return ����IIS�Ż���Ҫ�õ��ĳ���M
	 */
	public int generateFeatureFunction(List<String[]> xs, String[] ys) {
		funcs = new ArrayList<>();
		funcId2Int = new HashMap<>();
		int rowNum = xs.size();
		int M = 0;
		int count = 0;
		for(int i = 0; i < rowNum; i++) {
			String[] row = xs.get(i);
			String label = ys[i];
			int labelInt = y2id.get(label);
			for(String x : row) {
				int xInt = x2id.get(x);
				String funcId = FeatureFunction.generateFuncId(xInt, labelInt);
				Integer funcIndex = funcId2Int.get(funcId);
				FeatureFunction func;
				if(funcIndex == null) {
					func = new FeatureFunction(xInt, labelInt, 1);
					funcs.add(func);
					funcId2Int.put(funcId, count++);
					List<String> s = new ArrayList<>();
					M++;
				} else {
					func = funcs.get(funcIndex);
					M++;
					func.setWeight(func.getWeight() + 1);
				}
			}
		}
		return M;
	}
	/**
	 * ��ʼ������Ȩ��
	 */
	public void initParameter() {
		for(FeatureFunction featureFunction : funcs) {
			featureFunction.setWeight(0);
		}
	}
	/**
	 * IIS�Ż�
	 * @param maxIter ����������
	 * @param epsilon �������ȣ�����һ�ε�Ȩ�����
	 * @param xs x��������
	 * @param y y��ǩ
	 * @param M �Ż��õ���һ���������ó����������ݶ��½��е�ѧϰЧ�ʣ����Ծ���������Խ���Ż�Խ��
	 */
	public void IIS(int maxIter, double epsilon, List<String[]> xs, String[] y, int M) {
		int rowNum = xs.size();
		if(maxIter <= 0) {
			maxIter = 100000;
		}
		int count = 0;
		double[] priorExpectations = priorExpectation(rowNum);
		initParameter();
		double[] oldWeight = new double[funcs.size()];
		for(int i = 0; i < maxIter; i++) {
			System.out.println("iter..."+i);
			double[] modelExpectation = modelExpectation(xs);
			count = 0;
			//���²���
			for(FeatureFunction featureFunction : funcs) {
				double w = featureFunction.getWeight() + (1.0 / M) * Math.log(priorExpectations[count]/modelExpectation[count]);
				featureFunction.setWeight(w);
				count++;
			}
			//����Ƿ�����
			boolean isC = isconvergent(oldWeight, epsilon);
			if(isC) {
				System.out.println("��������");
				break;
			}
			//����ɵ�Ȩ��
			count = 0;
			for(FeatureFunction featureFunction : funcs) {
				oldWeight[count++] = featureFunction.getWeight();
			}
		}
	}
	/**
	 * �Ƿ�����
	 * @param oldWeight
	 * @param epsilon
	 * @return
	 */
	public boolean isconvergent(double[] oldWeight, double epsilon) {
		int count = 0;
		for(FeatureFunction featureFunction : funcs) {
			if(Math.abs(featureFunction.getWeight() - oldWeight[count++]) >= epsilon)
				return false;
		}
		return true;
	}
	/**
	 * ������������
	 * @param rowNum
	 * @return
	 */
	public double[] priorExpectation(int rowNum) {
		double[] priorExpectations = new double[funcs.size()];
		int count = 0;
		for(FeatureFunction featureFunction : funcs) {
			priorExpectations[count++] = featureFunction.getWeight() / rowNum;
		}
		return priorExpectations;
	}
	/**
	 * ����ģ������
	 * @param xs
	 * @return
	 */
	public double[] modelExpectation( List<String[]> xs) {
		int rowNum = xs.size();
		double[] expectations = new double[funcs.size()];
		for(int i = 0; i < rowNum; i++) {
			//��ȡ��ǰ����������x����
			String[] xss = xs.get(i);
			//����p(y|x)
			double[] pyGivenx = predict(xss);
			for(String x : xss) {
				int xId = x2id.get(x);
				for(int j = 0; j < id2y.length; j++) {
					String funcId = FeatureFunction.generateFuncId(xId, j);
					Integer funcIndex = funcId2Int.get(funcId);
					if(funcIndex != null) {
						expectations[funcIndex] += pyGivenx[j] * (1.0 / rowNum);
					}
				}
			}
		}
		return expectations;
	}
	public double[] predict(String[] x) {
		int[] xid = new int[x.length];
		//�п��ܳ���δ֪��������ֱ������
		for(int i = 0; i < x.length; i++) {
			Integer xIntid = x2id.get(x[i]);
			if(xIntid != null)
				xid[i] = xIntid;
		}
		return predict(xid);
	}
	
	/**
	 * ����p(y|x)
	 * ��ģ�Ͳ�����Ԥ�ⲻͬ��ǩ�ĸ��ʣ�����һ�����ֵ
	 * @param x
	 * @return
	 */
	public double[] predict(int[] x) {
		double[] prob = new double[id2y.length];
		//��һ������
		double z = 0;
		for(int y = 0; y < id2y.length; y++) {
			List<FeatureFunction> cfuncs = getFeatureFunction(x, y);
			prob[y] = Math.exp(computeWeight(cfuncs));
			z += prob[y];
		}
		//��һ��
		for(int i = 0; i < prob.length; i++) {
			prob[i] = prob[i] / z;
		}
		return prob;
	}
	/**
	 * Ȩ���ۼ�
	 * @param funcs
	 * @return
	 */
	public double computeWeight(List<FeatureFunction> funcs) {
		//��������Ȩ���ۼ�
		double w = 0;
		for(FeatureFunction featureFunction : funcs) {
			w += featureFunction.getWeight();
		}
		return w;
	}
	/**
	 * ����x��y��ȡ��Ӧ����������
	 * @param x
	 * @param y
	 * @return
	 */
	public List<FeatureFunction> getFeatureFunction(int[] x, int y){
		List<FeatureFunction> rfuncs = new ArrayList<>();
		for(int xx : x) {
			String funcId = FeatureFunction.generateFuncId(xx, y);
			//���ܳ���x�������������У���ʱ������ֱ��ѡ�����
			Integer funcIndex = funcId2Int.get(funcId);
			if(funcIndex != null) {
				rfuncs.add(funcs.get(funcIndex));
			}
		}
		return rfuncs;
	}
	public float accuracy(int[] yTrue, int[] yPredict) {
		int count = 0;
		for(int i = 0; i < yTrue.length; i++) {
			if(yPredict[i] == yTrue[i]) count++;
		}
		return (float) (count*1.0 / yTrue.length);
	}
	
	public int[] labels2Id(String[] labels) {
		int[] yids = new int[labels.length];
		for(int i = 0; i < yids.length; i++) {
			yids[i] = y2id.get(labels[i]);
		}
		return yids;
	}
	public int[] predict(String[][] nSamples) {
		int[] res = new int[nSamples.length];
		for(int i = 0; i < res.length; i++) {
			double[] prob = predict(nSamples[i]);
			res[i] = maxProbabilityClass(prob);
		}
		return res;
	}
	//�������ʵ����
	public int maxProbabilityClass(double[] prob) {
		double maxP = 0;
		int maxIndex = 0;
		for(int i = 0; i < prob.length; i++) {
			if(prob[i] > maxP) {
				maxP = prob[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}
}
