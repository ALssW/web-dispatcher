package com.alva.dispatcher.entity;

import java.util.List;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-09
 */public class Page<T> {

	private Integer pageSize;
	private Integer nowPage;
	private Integer maxPage;
	private List<T> list;

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getNowPage() {
		return nowPage;
	}

	public void setNowPage(Integer nowPage) {
		this.nowPage = nowPage;
	}

	public Integer getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(Integer maxPage) {
		if (maxPage <= 0) {
			this.maxPage = 0;
			return;
		}
		this.maxPage = maxPage;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
}
