<%@page import="java.util.*"%><jsp:useBean id="commonBean" scope="session" class="ru.flooring_nn.beans.CommonBean"/>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
HashMap <String, String> pars = new HashMap<String,String>(); 
//request.setCharacterEncoding("UTF-8");
for(Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
	String name = (String) params.nextElement();
	if(!"_".equals(name)) {
		pars.put(name, request.getParameter(name)); 
//				new String(request.getParameter(name).getBytes("ISO-8859-1"),"UTF-8"));
//		out.println(name+"= "+pars.get(name)+"\r\n");
	}
	commonBean.setPars(pars);
}
commonBean.setLevel(); 
commonBean.setPage();
int level = commonBean.getLevel();
int page_ = commonBean.getPage();
String id_parrent = commonBean.getPars().get("id");
String [] names = {"SECTION", "FIRMA", "COLLECTION", "DECOR","DECOR"};
//Vector<HashMap<String, String>> catalog = commonBean.getResult();
HashMap<String, Vector<HashMap<String, String>>> result = commonBean.getResult();
Vector<HashMap<String, String>> catalog = result.get("Request_0");
Vector<HashMap<String, String>> catalog_pred = result.get("Request_1");
HashMap <String, String> rows = (HashMap <String, String>) catalog.lastElement();
int countRows = Integer.parseInt(rows.get("ROWS"));
int pages = (int) Math.ceil(countRows / (commonBean.getPageSize()*1.0));
catalog.remove(catalog.lastElement());
if(level > 0) {
String id_cat_parent = names[level-1]+id_parrent;
%>
<script type="text/javascript">
var id_cat = '<%=id_cat_parent%>';
//$("#"+id_cat).parent().parent().find("a").css("font-weight","normal");
//alert("#"+id_cat+"\r\n"+$("#"+id_cat).parent().parent().html());
$("#"+id_cat).parent().parent().find("a").removeAttr("class");
$("#"+id_cat).children().attr("class","ovalBorder5");
//$("#"+id_cat).children().css("font-weight","bold");
//var parent = $("#"+id_cat).text();
//alert($("#"+id_cat).parent().html());
//$("#catalogArea caption h3").append("/<a href='#'>"+parent+"</a>");
<%if(level<3) {%>
$("#"+id_cat).parent().find("ul").remove();
$("#"+id_cat).html($("#"+id_cat).html()+"<ul>");
<%}%>
</script>
<%} else {%>
<script type="text/javascript">
$("#catalog").find("ul").remove();
$("#catalog").find("a").removeAttr("class");
</script>
<%} %>
<div id="catalogArea">
			 		<table>
				 		<caption>
				 			<h3>
<%
if(catalog_pred!=null) {
String [] sect = {catalog_pred.firstElement().get(names[0]), catalog_pred.firstElement().get("ID_"+names[0])};
String [] firm = {catalog_pred.firstElement().get(names[1]), catalog_pred.firstElement().get("ID_"+names[1])};
String [] coll = {catalog_pred.firstElement().get(names[2]), catalog_pred.firstElement().get("ID_"+names[2])};
String [] dec = {catalog_pred.firstElement().get(names[3]), catalog_pred.firstElement().get("ID_"+names[3])};
%>
								<nobr>				 			
					 				<a href="#" onclick="loadCatalog(0, 0);">Каталог товаров</a>
					 				<span>
						 				<%	if(sect != null && sect[0]!=null) {%>
						 				/<a href="#" onclick="loadCatalog(<%=sect[1]%>, 1);"><%=sect[0] %></a>
						 				<%} 
						 					if(firm!=null && firm[0] != null) {%>
						 				/<a href="#" onclick="loadCatalog(<%=firm[1]%>, 2);"><%=firm[0] %></a>
						 				<%} 
						 					if(coll!=null && coll[0] != null) {%>
						 				/<a href="#" onclick="loadCatalog(<%=coll[1]%>, 3);"><%=coll[0] %></a>
						 				<%} 
						 					if(dec !=null && dec[0] != null) {%>
						 				/<a href="#" onclick="loadCatalog(<%=dec[1]%>, 4);"><%=dec[0] %></a>
						 				<%} %>
					 				</span>
				 				</nobr>
<%} else if("poisk".equals(pars.get("id"))) {
	String tov = commonBean.getNormPrefix(countRows, "товар");
%>

					 				Результат поиска:
					 				<%if(countRows==0) {%>
					 				Ничего не найдено
					 				<%} else { %> 
					 				<%=countRows %> <%=tov %>
<%									}
  } else {%>
					 				<a href="#" onclick="loadCatalog(0, 0);">Каталог товаров</a>
<%} %>
				 			</h3>
				 		</caption>
						<%	if(level <4) {
								int colsCatalog = 4; //количество столбцов каталоге
								int percentWidth = 100/colsCatalog;
								for(Enumeration elements = catalog.elements();elements.hasMoreElements();) {%>
									<tr>
							<%		for(int i=0; i<colsCatalog && elements.hasMoreElements(); i++) {
										HashMap <String, String> row = ((HashMap<String, String>) elements.nextElement());
										String nameColumn = names[level];
										String section = row.get(nameColumn);
										String picture = row.get("PICTURE");
										String id = row.get("ID");%>
										<td style='width : <%=percentWidth%>%'>
					 					<a href="#" onclick="loadCatalog(<%=id%>, <%=level+1%>);">
					 						<div>
					 							<img alt="<%=section%>" title="<%=section%>" src="<%=picture%>">
					 							<br/><%=section%>
					 						</div>
					 					</a>
					 					</td>
	<%if(level>0 && level<3) {%>
	<script type="text/javascript">
	<%String id_cat = names[level]+id;%>
	var id = "<%=id_cat%>";
	var section = '<%=section%>';
	//var countRows = <%=countRows%>;
	var ahref = '<a href="#" onclick="loadCatalog(<%=id%>, <%=level+1%>);" style="font-size : <%=13-level%>px;">';
	$("#"+id_cat+" ul").append("<li style='margin-left: -25px;list-style-type: disc;' id="+id+">"+ahref+section+" </a></li>");
	</script>
	<%} 
					 					 if(!elements.hasMoreElements()) {
					 						for(int j=i+1; j<colsCatalog; j++) {%>
										<td style='width : <%=percentWidth%>%'>
					 						<div>
					 							&nbsp;
					 						</div>
					 					</td>
					 							
					 						<%}
					 					} 
									}%>
									</tr>
									
						<%		}
							} else {
								for(Enumeration elements = catalog.elements();elements.hasMoreElements();) {
									HashMap <String, String> row = ((HashMap<String, String>) elements.nextElement());
									String nameColumn = names[level];
									String section = row.get(nameColumn);
									String picture = row.get("PICTURE");
									String price = row.get("PRICE");
									String id = row.get("ID");%>
								<tr>
									<td style='vertical-align: top;width : 50%'>
										<div>
					 						<img alt="<%=section%>" width="400px" height="280px" title="<%=section%>" src="<%=picture%>">
					 					</div>
										
									</td>
									<td style='vertical-align: top;width : 50%'>
										<table>
											<tr>
												<td>
													<div id='product'>
								 						<%=section%>
						 							</div>
						 						</td>
						 					</tr>
											<tr>
												<td>
													<%if("-1".equals(price)) { %>
														на заказ
													<%} else { %>
													<nobr>цена (м²):
													<span id='product'>
								 						<%=price%> руб.
						 							</span></nobr><br/>в наличии
							 						<%} %>
						 						</td>
						 					</tr>
										</table>
									</td>
								</tr>
						<%		}
							}%>				 		
				 		</table>
			 			<br/>
			 			<% if(level<4 && pages > 1) {%>
			 				<div id="pages">
			 			<%	for(int i = 1; i<=pages; i++) {
			 					if(i!=page_) {
			 						String id = pars.get("id");
			 						if("poisk".equals(id)) {%>
			 				&nbsp;<a href="#" onclick="loadCatalog('<%=id%>',3,<%=i%>);"><%=i%></a>&nbsp;
			 						<%} else { %>
			 				&nbsp;<a href="#" onclick="loadCatalog(<%=id_parrent%>, <%=level%>, <%=i%>);"><%=i%></a>&nbsp;
			 			<%			}
			 					} else {%>
			 						<span>&nbsp;<%=i%>&nbsp;</span>
			 			<%		}
			 				}%>
			 				</div>
				 			<br/>
				 			<br/>
			 			<%}%>
				 	</div>
