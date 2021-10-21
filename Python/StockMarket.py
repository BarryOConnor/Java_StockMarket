import suds
import tkinter as tk
from tkinter import ttk
from tkinter import messagebox as ms

from datetime import datetime
import json, os, math

class StockWebService:
    def __init__(self):
        self.client = suds.client.Client("http://localhost:8080/StockWebService/StockWebService?WSDL")

    def sort(self, sortField):
        return self.client.service.sort("companyname_asc")

class StockMarket(tk.Tk):

    def __init__(self, *args, **kwargs):
        
        tk.Tk.__init__(self, *args, **kwargs)
        self.geometry("1024x768") # set window to 1024x768
        self.title("Stock Market")# set window title

        style = ttk.Style()
        style.element_create("Custom.Treeheading.border", "from", "default")
        style.layout("Custom.Treeview.Heading", [
            ("Custom.Treeheading.cell", {'sticky': 'nswe'}),
            ("Custom.Treeheading.border", {'sticky':'nswe', 'children': [
                ("Custom.Treeheading.padding", {'sticky':'nswe', 'children': [
                    ("Custom.Treeheading.image", {'side':'right', 'sticky':''}),
                    ("Custom.Treeheading.text", {'sticky':'we'})
                ]})
            ]}),
        ])
        style.configure('Custom.Treeview', highlightthickness=0, bd=0, font=('Helvetica', 10))
        style.configure('Custom.Treeview.Heading', font=('Helvetica', 10, 'bold'), background="#9E7BB8", foreground="white")
        style.map('Treeview', background=[('selected', '#BFBFBF')])
        #style.configure("Custom.Treeview.Heading", background="blue", foreground="white", relief="flat")

        container = tk.Frame(self) #create a container for all the different forms (frames)
        container.place(y=0, x=0, width=1024, height=768) #place under the logo so only this frame changes

        menubar = tk.Menu(container)
        tk.Tk.config(self, menu=menubar)
        fileMenu = tk.Menu(menubar, tearoff=0)
        menubar.add_cascade(label="File", menu=fileMenu)
        fileMenu.add_command(label="Exit", command=quit)

        self.frames = {}

        frame = MainScreen(container, self)
        self.frames[MainScreen] = frame
        frame.place(y=0, x=0, width=1024, height=768)
            

        self.show_frame(MainScreen) 



    def show_frame(self, cont):

        frame = self.frames[cont]
        frame.tkraise()
        frame.event_generate("<<showframe>>")
        menubar = frame.menubar(self)
        self.configure(menu=menubar)

class MainScreen(tk.Frame):

    def __init__(self, parent, controller):
        
        self.StockWebService = suds.client.Client("http://localhost:8080/StockWebService/StockWebService?WSDL")
        tk.Frame.__init__(self, parent)
        self.controller = controller #sets the controller to be referenced in the instance so you can use it again later
        self.bind("<Return>", self.register)
        self.focus_set()

        

        # variables
        self.currency = "GBP"
        self.selected_item = ""
        self.exchange_rate = 1
        self.curr_company = ""
        self.current_sort = "companyname_asc"
        self.searchfield = ""
        self.searchtype = ""
        self.fields = ["Company Name", "Symbol", "No of Shares", "Share Price"]
        self.alpha = ["Contains", "Begins", "Ends", "Equals"]
        self.numeric = ["=", ">", "<", ">=", "<=", "between"]

        # currency combo
        self.lbl_currency = tk.Label(self, width=10, anchor=tk.NE, text="Currency")
        self.lbl_currency.place(y=10, x=285, anchor=tk.NW, width=100)
        self.cbo_currency = ttk.Combobox(self, textvariable=self.currency, state="readonly")
        self.cbo_currency["values"] = self.StockWebService.service.getCurrencyListFormatted()
        self.cbo_currency.current(15)
        self.cbo_currency.place(y=10, x=395, anchor=tk.NW, width=300)
        self.cbo_currency.bind("<<ComboboxSelected>>", self.update_currency)


        # Stock Table
        self.share_table = ttk.Treeview(self, style="Custom.Treeview", column=("company_name", "symbol", "no_of_shares", "share_price", "last_updated"), selectmode="browse", show="headings")
        self.share_table.heading("company_name", text="Company Name", command=lambda: self.sort(self, "companyname"))
        self.share_table.column("company_name", minwidth=350, width=350, stretch=tk.NO)
        self.share_table.heading("symbol", text="Stock Symbol", command=lambda: self.sort(self, "symbol"))
        self.share_table.column("symbol", minwidth=100, width=100, stretch=tk.NO)
        self.share_table.heading("no_of_shares", text="Number of Shares", command=lambda: self.sort(self, "noofshares"))
        self.share_table.column("no_of_shares", minwidth=100, width=150, stretch=tk.NO)
        self.share_table.heading("share_price", text="Share Price", command=lambda: self.sort(self, "shareprice"))
        self.share_table.column("share_price", minwidth=100, width=150, stretch=tk.NO)
        self.share_table.heading("last_updated", text="Last Updated", command=lambda: self.sort(self, "lastupdated"))
        self.share_table.column("last_updated", minwidth=350, width=250, stretch=tk.NO)
        self.share_table.place(x=7, y=40, width=990, height=300)
        vsb = ttk.Scrollbar(self, orient="vertical", command=self.share_table.yview)
        vsb.place(x=997, y=41, height=298)
        self.share_table.bind("<<TreeviewSelect>>", self.select_stock)

    
        # search frame contents
        self.lbfrm_buysell = ttk.LabelFrame(self, width = 1000, height = 110)
        self.lbfrm_buysell.config(padding = (10, 10))
        self.lbfrm_buysell.config(text = "Buy & Sell")
        self.lbfrm_buysell.place(y=360, x=10, anchor=tk.NW, width=1000)
        self.lbl_buy = tk.Label(self.lbfrm_buysell, width=10, anchor=tk.NE,  text="Buy shares in this company")
        self.lbl_buy.place(y=0, x=250, width=150)
        self.lbl_sell = tk.Label(self.lbfrm_buysell, width=10, anchor=tk.E,  text="Sell shares in this company")
        self.lbl_sell.place(y=40, x=250, width=150)
        self.txt_buy = tk.Entry(self.lbfrm_buysell, width=30)    
        self.txt_buy.place(y=0, x=420, anchor=tk.NW, width=60)
        self.txt_sell = tk.Entry(self.lbfrm_buysell, width=30)
        self.txt_sell.place(y=40, x=420, anchor=tk.NW, width=60)
        self.btn_buy = tk.Button(self.lbfrm_buysell,text="Buy",width=15,command=lambda: self.buy(self))
        self.btn_buy.place(y=00, x=500, anchor=tk.NW, width=120)
        self.btn_sell = tk.Button(self.lbfrm_buysell,text="Sell", width=15, default="active", command=lambda: self.sell(self))
        self.btn_sell.place(y=40, x=500, anchor=tk.NW, width=120)


        # search frame contents
        self.lbfrm_search = ttk.LabelFrame(self, width = 1000, height = 70)
        self.lbfrm_search.config(padding = (10, 10))
        self.lbfrm_search.config(text = "Search")
        self.lbfrm_search.place(y=490, x=10, anchor=tk.NW, width=1000)
        self.lbl_buy = tk.Label(self.lbfrm_search, width=10, anchor=tk.NE,  text="Search for shares where")
        self.lbl_buy.place(y=0, x=150, width=150)
        self.cbo_field = ttk.Combobox(self.lbfrm_search, textvariable=self.searchfield, state="readonly")
        self.cbo_field["values"] = self.fields
        self.cbo_field.current(0)
        self.cbo_field.place(y=0, x=330, anchor=tk.NW, width=150)
        self.cbo_field.bind("<<ComboboxSelected>>", self.field_changed)
        self.cbo_search = ttk.Combobox(self.lbfrm_search, textvariable=self.searchtype, state="readonly")
        self.cbo_search["values"] = self.alpha
        self.cbo_search.current(0)
        self.cbo_search.place(y=0, x=500, anchor=tk.NW, width=100)
        self.cbo_search.bind("<<ComboboxSelected>>", self.search_changed)
        self.search_one = tk.Entry(self.lbfrm_search, width=30)    
        self.search_one.place(y=0, x=630, anchor=tk.NW, width=60)
        self.search_two = tk.Entry(self.lbfrm_search, width=30)    
        self.search_two.place(y=0, x=-700, anchor=tk.NW, width=60)
        self.btn_search = tk.Button(self.lbfrm_search,text="Search",width=15,command=lambda: self.search(self))
        self.btn_search.place(y=00, x=780, anchor=tk.NW, width=120)
        


        #setup update event for the frame - runs when switching to the frame 
        self.bind("<<showframe>>", self.update_stock)


    @staticmethod
    def display_values(self, shares):
        self.share_table.delete(*self.share_table.get_children()) 
        count = 0
        selection_found = False
        for share in shares:
            row = [share.company_name, share.symbol, share.no_of_shares, "{:.2f}".format(share.share_price.value * self.exchange_rate), share.share_price.last_updated.strftime("%x %X") ]
            if self.selected_item == "":
                self.selected_item = 'Row'+share.symbol
            if self.selected_item == 'Row'+share.symbol:
                selection_found = True
            self.share_table.insert("", count, iid='Row'+share.symbol, values=row)
            count += 1
        if selection_found:
            self.share_table.focus_set()
            self.share_table.selection_set(self.selected_item)


    @staticmethod   
    def update_stock_values(self):
        #updates the tree with data from the database whenever the date changes
        stocks = self.StockWebService.service.sort(self.current_sort)
        if stocks.operationValid:             
            self.display_values(self, stocks.resultsList)      
            

    @staticmethod   
    def buy(self):
        # buy stock and updates the table
        if float(self.txt_buy.get()).is_integer():
            stocks = self.StockWebService.service.buy(self.curr_company, self.txt_buy.get())
            if stocks.operationValid:
                self.display_values(self, stocks.resultsList)       
        else:
            ms.showerror("Error","Please enter a number")

    @staticmethod   
    def sell(self):
        # sells stock and updates the table
        if float(self.txt_sell.get()).is_integer():
            stocks = self.StockWebService.service.sell(self.curr_company, self.txt_sell.get())
            if stocks.operationValid:
                self.display_values(self, stocks.resultsList)
        else:
            ms.showerror("Error","Please enter a number")
            

    @staticmethod   
    def sort(self, column):
        if self.current_sort.find(column) != -1:
            if self.current_sort.find("_asc") != -1:
                self.current_sort = column + "_desc"
            else:
                self.current_sort = column + "_asc" 
        else:
            self.current_sort = column + "_asc"
        self.update_stock_values(self)

        

    @staticmethod   
    def search(self):
        field = self.cbo_field.get().lower()
        field = field.replace(" ", "")
        search = self.cbo_search.get().lower()
        if field == "noofshares":
            if search == "between":
                try:
                    resultone = float(self.search_one.get())
                    resulttwo = float(self.search_two.get())
                    if (not resultone.is_integer()) or (not resulttwo.is_integer()):
                        ms.showerror("Error","Search values must be numeric")
                        return
                    else:
                        self.share_table.delete(*self.share_table.get_children())        
                        stocks = self.StockWebService.service.searchBetween(field, self.search_one.get(), self.search_two.get())
                except ValueError:
                    ms.showerror("Error","Search values must be numeric")
                    return
                
            else:
                try:
                    resultone = float(self.search_one.get())
                    if not resultone.is_integer():
                        ms.showerror("Error","Search values must be numeric")
                        return
                    else:
                        self.share_table.delete(*self.share_table.get_children())        
                        stocks = self.StockWebService.service.search(field, search, self.search_one.get())
                except ValueError:
                    ms.showerror("Error","Search values must be numeric")
                    return
                
        elif field == "shareprice":
            if search == "between":
                try:
                    resultone = float(self.search_one.get())
                    resulttwo = float(self.search_two.get())
                    self.share_table.delete(*self.share_table.get_children())        
                    stocks = self.StockWebService.service.searchBetween(field, self.search_one.get(), self.search_two.get())
                except ValueError:
                    ms.showerror("Error","Search values must be numeric")
                    return
            else:
                try:
                    resultone = float(self.search_one.get())
                    self.share_table.delete(*self.share_table.get_children())        
                    stocks = self.StockWebService.service.search(field, search, self.search_one.get())
                except ValueError:
                    ms.showerror("Error","Search values must be numeric")
                    return                
        else:
            if self.search_one.get() == "":
                ms.showerror("Error","The search value cannot be blank")
                return
            self.share_table.delete(*self.share_table.get_children())        
            stocks = self.StockWebService.service.search(field, search, self.search_one.get())
        if stocks.operationValid:
            self.display_values(self, stocks.resultsList)   
        

    def update_stock(self, parent):
        #update the 
        self.update_stock_values(self)
        

    def select_stock(self, parent):
        # onclick for the table to select the current stock item
        self.curr_company = self.share_table.item(self.share_table.selection())["values"][1]
        for item in self.share_table.selection():
            self.selected_item = item

    def field_changed(self, parent):
        # the field combo has changed so show/hide the second input as needed and update the contents of the search combo

        if self.cbo_field.get() == "Company Name" or self.cbo_field.get() == "Symbol":
            self.cbo_search["values"] = self.alpha
            self.cbo_search.current(0)
            self.search_two.place(y=0, x=-700, anchor=tk.NW, width=60)
        else:
            self.cbo_search["values"] = self.numeric
            self.cbo_search.current(0)


    def search_changed(self, parent):
        # the search combo has changed so show/hide the second input as needed
        if self.cbo_search.get() == "between":
            self.search_two.place(y=0, x=700, anchor=tk.NW, width=60)
        else:
            self.search_two.place(y=0, x=-700, anchor=tk.NW, width=60)


    def update_currency(self, parent):
        # update the currency conversion
        self.currency = self.cbo_currency.get()[0:3]        
        self.exchange_rate = self.StockWebService.service.getExchangeRate(self.currency)
        self.update_stock_values(self)

    def menubar(self, parent):
        # menu handler
        menubar = tk.Menu(parent)
        filemenu = tk.Menu(menubar, tearoff=0)
        filemenu.add_command(label="Exit", command=quit)
        menubar.add_cascade(label="File", menu=filemenu)
        return menubar
    
    

        


app = StockMarket()
app.mainloop()
