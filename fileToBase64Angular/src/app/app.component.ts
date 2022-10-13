import {Component} from '@angular/core';
import {NzMessageService, UploadFile} from 'ng-zorro-antd';
import {HttpClient, HttpRequest, HttpResponse} from '@angular/common/http';
import {filter} from 'rxjs/operators';
import {AsyncSubject, Observable} from 'rxjs';


export interface SelectedFiles {
  name: string;
  file: any;
  base64?: string;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'fileToBase64Angular';

  uploading = false;
  fileList: UploadFile[] = [];
  disabled = false;

  constructor(private http: HttpClient, private msg: NzMessageService) {
  }


  public selectedFiles: SelectedFiles[] = [];

  public toFilesBase64(files: File[], selectedFiles: SelectedFiles[]): Observable<SelectedFiles[]> {
    const result = new AsyncSubject<SelectedFiles[]>();
    if (files?.length) {
      Object.keys(files)?.forEach(async (file, i) => {
        const reader = new FileReader();
        reader.readAsDataURL(files[i]);
        reader.onload = (e) => {
          selectedFiles = selectedFiles?.filter(f => f?.name !== files[i]?.name);
          selectedFiles.push({name: files[i]?.name, file: files[i], base64: reader?.result as string});
          result.next(selectedFiles);
          if (files?.length === (i + 1)) {
            result.complete();
          }
        };
      });
      return result;
    } else {
      result.next([]);
      result.complete();
      return result;
    }
  }

  public onFileSelected(files: File[]) {
    // this.selectedFiles = []; // clear
    this.toFilesBase64(files, this.selectedFiles).subscribe((res: SelectedFiles[]) => {
      this.selectedFiles = res;
      console.log(this.selectedFiles);
    });
  }


  beforeUpload = (file: UploadFile): boolean => {
    this.fileList = this.fileList.concat(file);
    return false;
  };

  handleUpload(): void {
    const formData = new FormData();
    // tslint:disable-next-line:no-any
    this.fileList.forEach((file: any) => {
      console.log(file);
      formData.append('files[]', file);
      const result = new AsyncSubject<SelectedFiles[]>();
      let selectedFiles: SelectedFiles[] = [];


      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = (e) => {
        selectedFiles = selectedFiles?.filter(f => f?.name !== file?.name);
        selectedFiles.push({name: file?.name, file, base64: reader?.result as string});
        result.next(selectedFiles);
        console.log('base', selectedFiles[0].base64);
        // if (files?.length === (i + 1)) {
        //   result.complete();
        // }

        this.uploading = true;
        // // You can use any AJAX library you like
        // const req = new HttpRequest('POST', '/api/carga', {
        //   base64: selectedFiles[0].base64
        // });

        const req = new HttpRequest('POST', '/api/encode', {
          base64: selectedFiles[0].base64
        });

        this.http
          .request(req)
          .pipe(filter(e => e instanceof HttpResponse))
          .subscribe(
            () => {
              this.uploading = false;
              this.fileList = [];
              this.msg.success('upload successfully.');
            },
            () => {
              this.uploading = false;
              this.msg.error('upload failed.');
            }
          );
      };
    });
  }


}
